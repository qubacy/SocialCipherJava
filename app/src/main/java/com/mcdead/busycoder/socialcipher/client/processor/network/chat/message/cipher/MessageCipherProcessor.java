package com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.cipher;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSession;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.set.CipherSessionStateSet;
import com.mcdead.busycoder.socialcipher.cipher.data.storage.CipherSessionStore;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererBase;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class MessageCipherProcessor {
    public static final String C_CIPHERED_TEXT_PREFIX = "[[SCCT]]";
    public static final String C_DECIPHERED_TEXT_PREFIX = "[[SSDT]]";

    public static final String C_DECIPHERED_ATTACHMENT_DATA_PREFIX = "[[SSDA]]";
    public static final int C_CIPHERED_ATTACHMENT_HEADER_MIN_LENGTH_BYTES =
            Integer.BYTES + C_DECIPHERED_ATTACHMENT_DATA_PREFIX.length() * 2;
    public static final String C_CIPHERED_ATTACHMENT_EXT = "sc";

    public static final String C_CIPHERED_ATTACHMENT_MIME_TYPE = "application/sc";

    final private CiphererBase m_cipherer;
    final private File m_cacheDir;

    private MessageCipherProcessor(
            final CiphererBase cipherer,
            final File cacheDir)
    {
        m_cipherer = cipherer;
        m_cacheDir = cacheDir;
    }

    public static MessageCipherProcessor getInstance(
            final long chatId)
    {
        if (chatId == 0) return null;

        SettingsSystem settingsSystem = SettingsSystem.getInstance();

        if (settingsSystem == null)
            return null;

        File cacheDir = settingsSystem.getCacheDir();

        if (cacheDir == null)
            return null;

        CipherSessionStore cipherSessionStore = CipherSessionStore.getInstance();

        if (cipherSessionStore == null)
            return null;

        CipherSession cipherSession = cipherSessionStore.getSessionByChatId(chatId);

        if (cipherSession == null)
            return null;
        if (!(cipherSession.getState() instanceof CipherSessionStateSet))
            return null;

        CipherSessionStateSet cipherSessionStateSet =
                (CipherSessionStateSet) cipherSession.getState();

        return new MessageCipherProcessor(cipherSessionStateSet.getCipherer(), cacheDir);
    }

    public Error processText(
            final String sourceText,
            final boolean isEncrypting,
            ObjectWrapper<Pair<Boolean, String>> resultSuccessFlagTextWrapper)
    {
        if (sourceText == null)
            return new Error("Provided Source Text was null!", true);

        StringBuilder processedTextBuilder = new StringBuilder();

        if (isEncrypting) {
            StringBuilder prefixedText = new StringBuilder(C_DECIPHERED_TEXT_PREFIX);

            prefixedText.append(sourceText);

            byte[] processedBytes =
                    m_cipherer.encryptBytes(prefixedText.toString().getBytes(StandardCharsets.UTF_8));

            if (processedBytes == null)
                return new Error("Processed bytes were equal to null!", true);

            processedTextBuilder.append(C_CIPHERED_TEXT_PREFIX);
            processedTextBuilder.append(Base64.getEncoder().encodeToString(processedBytes));

        } else {
            int prefixIndex = sourceText.indexOf(C_CIPHERED_TEXT_PREFIX);

            if (prefixIndex != 0) {
                resultSuccessFlagTextWrapper.setValue(new Pair<>(false, sourceText));

                return null;
            }

            String contentString = sourceText.substring(C_CIPHERED_TEXT_PREFIX.length());
            byte[] processedBytes = Base64.getDecoder().decode(contentString);

            if (processedBytes == null)
                return new Error("Processed bytes were equal to null!", true);

            byte[] decipheredBytes = m_cipherer.decryptBytes(processedBytes);

            if (decipheredBytes == null)
                return new Error("Deciphered bytes were equal to null!", true);

            String decipheredText = new String(decipheredBytes, StandardCharsets.UTF_8);

            prefixIndex = decipheredText.indexOf(C_DECIPHERED_TEXT_PREFIX);

            if (prefixIndex != 0) {
                resultSuccessFlagTextWrapper.setValue(new Pair<>(false, sourceText));

                return null;
            }

            processedTextBuilder.append(
                    decipheredText.substring(C_DECIPHERED_TEXT_PREFIX.length()));
        }

        String processedText = processedTextBuilder.toString();

        if (processedText.isEmpty())
            return new Error("Processed Text was empty!", true);

        resultSuccessFlagTextWrapper.setValue(new Pair<>(true, processedText));

        return null;
    }

    public Error decipherAttachmentBytes(
            final byte[] attachmentBytes,
            ObjectWrapper<AttachmentDecipheringResult> attachmentDecipherResultWrapper)
    {
        if (attachmentBytes == null)
            return new Error("Attachment's bytes were equal to null!", true);
        if (attachmentBytes.length <= 0)
            return new Error("Attachment's bytes array was empty!", true);

        byte[] decipheredBytes = m_cipherer.decryptBytes(attachmentBytes);

        if (decipheredBytes == null)
            return new Error("Deciphered Attachment's bytes were equal to null!", true);
        if (decipheredBytes.length < C_CIPHERED_ATTACHMENT_HEADER_MIN_LENGTH_BYTES) {
            attachmentDecipherResultWrapper.setValue(new AttachmentDecipheringResult());

            return null;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(decipheredBytes);
        byte[] prefixLabelBytes =
                new byte[C_DECIPHERED_ATTACHMENT_DATA_PREFIX.getBytes(StandardCharsets.UTF_8).length];

        byteBuffer.get(prefixLabelBytes);

        String decipheredPrefix = new String(prefixLabelBytes, StandardCharsets.UTF_8);

        if (decipheredPrefix.compareTo(C_DECIPHERED_ATTACHMENT_DATA_PREFIX) != 0) {
            attachmentDecipherResultWrapper.setValue(new AttachmentDecipheringResult());

            return null;
        }

        int attachmentTypeId = byteBuffer.getInt();
        AttachmentType attachmentType = AttachmentType.getTypeById(attachmentTypeId);

        if (attachmentType == null) {
            attachmentDecipherResultWrapper.setValue(new AttachmentDecipheringResult());

            return null;
        }

        byte[] endingHeaderPrefixBytes = new byte[C_DECIPHERED_ATTACHMENT_DATA_PREFIX.length()];
        int startPosition = byteBuffer.position();
        int extensionBytesLength = 0;

        for (int i = startPosition; i < decipheredBytes.length - C_DECIPHERED_ATTACHMENT_DATA_PREFIX.length(); ++i) {
            byteBuffer.get(endingHeaderPrefixBytes);

            String endingHeaderPrefix = new String(endingHeaderPrefixBytes, StandardCharsets.UTF_8);

            if (endingHeaderPrefix.compareTo(C_DECIPHERED_ATTACHMENT_DATA_PREFIX) == 0) {
                extensionBytesLength = i - startPosition;
                byteBuffer.position(startPosition);

                break;
            }

            byteBuffer.position(i + 1);
        }

        byte[] extensionBytes = new byte[extensionBytesLength];

        byteBuffer.get(extensionBytes);

        String extension = new String(extensionBytes, StandardCharsets.UTF_8);

        byteBuffer.get(endingHeaderPrefixBytes);

        byte[] contentBytes =
                Arrays.copyOfRange(decipheredBytes, byteBuffer.position(), decipheredBytes.length);

        attachmentDecipherResultWrapper.setValue(
                new AttachmentDecipheringResult(
                        true, attachmentType, extension, contentBytes));

        return null;
    }

    public Error cipherAttachmentData(
            final ContentResolver contentResolver,
            final AttachmentData sourceAttachmentData,
            ObjectWrapper<AttachmentData> resultAttachmentDataListWrapper)
    {
        if (sourceAttachmentData == null)
            return new Error("Provided Source Attachment Data List was null!", true);

        // todo: getting source bytes..

        byte[] sourceBytes = null;

        try (InputStream in = contentResolver.openInputStream(sourceAttachmentData.getUri())) {
            int availableByteCount = in.available();
            String sourceExtension =
                    AttachmentContext.getExtensionByFileName(sourceAttachmentData.getFileName());

            if (sourceExtension == null)
                return new Error("Cannot grasp the provided attachment's extension!", true);

            int sourceExtensionBytesCount = sourceExtension.length();
            ByteBuffer bytes = ByteBuffer.allocate(availableByteCount + C_CIPHERED_ATTACHMENT_HEADER_MIN_LENGTH_BYTES + sourceExtensionBytesCount);

            bytes.put(C_DECIPHERED_ATTACHMENT_DATA_PREFIX.getBytes(StandardCharsets.UTF_8));
            bytes.putInt(sourceAttachmentData.getType().getId());
            bytes.put(sourceExtension.getBytes(StandardCharsets.UTF_8));
            bytes.put(C_DECIPHERED_ATTACHMENT_DATA_PREFIX.getBytes(StandardCharsets.UTF_8));

            sourceBytes = bytes.array(); // todo: too much memory consumption..

            if (in.read(sourceBytes, C_CIPHERED_ATTACHMENT_HEADER_MIN_LENGTH_BYTES + sourceExtensionBytesCount, availableByteCount)
                    != availableByteCount)
            {
                return new Error("Read attachment's bytes are not full!", true);
            }

        } catch (Exception e) {
            e.printStackTrace();

            return new Error("Ciphered Attachment file's stream hasn't been created!", true);
        }

        byte[] cipheredBytes = m_cipherer.encryptBytes(sourceBytes);

        if (cipheredBytes == null)
            return new Error("Ciphered Attachment's bytes were equal to null!", true);

        // todo: writing ciphered bytes to the cache..

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(
                AttachmentContext.getAttachmentIdByFileName(sourceAttachmentData.getFileName()));
        stringBuilder.append('.');
        stringBuilder.append(C_CIPHERED_ATTACHMENT_EXT);

        String cachedCipheredAttachmentFileName = stringBuilder.toString();
        File cachedCipheredAttachment =
                new File(
                        m_cacheDir,
                        cachedCipheredAttachmentFileName);

        try {
            if (!cachedCipheredAttachment.exists())
                if (!cachedCipheredAttachment.createNewFile())
                    return new Error("Ciphered Attachment file hasn't been created!", true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream out = new FileOutputStream(cachedCipheredAttachment)) {
            out.write(cipheredBytes);

        } catch (Exception e) {
            e.printStackTrace();

            return new Error("Ciphered Attachment file's stream hasn't been created!", true);
        }

        AttachmentData cipheredAttachmentData =
                new AttachmentData(
                        AttachmentType.DOC,
                        C_CIPHERED_ATTACHMENT_MIME_TYPE,
                        cachedCipheredAttachmentFileName,
                        Uri.fromFile(cachedCipheredAttachment));

        resultAttachmentDataListWrapper.setValue(cipheredAttachmentData);

        return null;
    }

    public static class AttachmentDecipheringResult {
        final private boolean m_isSuccessful;
        final private AttachmentType m_attachmentType;
        final private String m_fileExtension;
        final private byte[] m_bytes;

        public AttachmentDecipheringResult(
                final boolean isSuccessful,
                final AttachmentType attachmentType,
                final String fileExtension,
                final byte[] bytes)
        {
            m_isSuccessful = isSuccessful;
            m_attachmentType = attachmentType;
            m_fileExtension = fileExtension;
            m_bytes = bytes;
        }

        public AttachmentDecipheringResult() {
            m_isSuccessful = false;
            m_attachmentType = null;
            m_fileExtension = null;
            m_bytes = null;
        }

        public boolean isSuccessful() {
            return m_isSuccessful;
        }

        public AttachmentType getAttachmentType() {
            return m_attachmentType;
        }

        public String getFileExtension() {
            return m_fileExtension;
        }

        public byte[] getBytes() {
            return m_bytes;
        }
    }
}
