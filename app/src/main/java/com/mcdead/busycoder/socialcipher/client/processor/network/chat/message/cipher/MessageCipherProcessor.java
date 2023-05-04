package com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.cipher;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSession;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.set.CipherSessionStateSet;
import com.mcdead.busycoder.socialcipher.cipher.data.storage.CipherSessionStore;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererBase;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class MessageCipherProcessor {
    public static final String C_CIPHERED_TEXT_PREFIX = "[[SCCT]]";

    final private CiphererBase m_cipherer;

    private MessageCipherProcessor(final CiphererBase cipherer) {
        m_cipherer = cipherer;
    }

    public static MessageCipherProcessor getInstance(
            final long chatId)
    {
        if (chatId == 0) return null;

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

        return new MessageCipherProcessor(cipherSessionStateSet.getCipherer());
    }

    public Error processText(
            final String sourceText,
            final boolean isEncrypting,
            ObjectWrapper<String> resultTextWrapper)
    {
        if (sourceText == null)
            return new Error("Provided Source Text was null!", true);

        StringBuilder processedTextBuilder = new StringBuilder();

        if (isEncrypting) {
            byte[] processedBytes = m_cipherer.encryptBytes(sourceText.getBytes(StandardCharsets.UTF_8));

            if (processedBytes == null)
                return new Error("Processed bytes were equal to null!", true);

            processedTextBuilder.append(C_CIPHERED_TEXT_PREFIX);
            processedTextBuilder.append(Base64.getEncoder().encodeToString(processedBytes));

        } else {
            int prefixIndex = sourceText.indexOf(C_CIPHERED_TEXT_PREFIX);

            if (prefixIndex != 0) {
                resultTextWrapper.setValue(sourceText);

                return null;
            }

            String contentString = sourceText.substring(C_CIPHERED_TEXT_PREFIX.length());
            byte[] processedBytes = Base64.getDecoder().decode(contentString);

            if (processedBytes == null)
                return new Error("Processed bytes were equal to null!", true);

            byte[] decipheredBytes = m_cipherer.decryptBytes(processedBytes);

            if (decipheredBytes == null)
                return new Error("Deciphered bytes were equal to null!", true);

            processedTextBuilder.append(new String(decipheredBytes, StandardCharsets.UTF_8));
        }

        String processedText = processedTextBuilder.toString();

        if (processedText.isEmpty())
            return new Error("Processed Text was empty!", true);

        resultTextWrapper.setValue(processedText);

        return null;
    }

    public Error processAttachmentData(
            final List<AttachmentData> sourceAttachmentDataList,
            final boolean isEncrypting,
            ObjectWrapper<List<AttachmentData>> resultAttachmentDataListWrapper)
    {
        if (sourceAttachmentDataList == null)
            return new Error("Provided Source Attachment Data List was null!", true);
        if (sourceAttachmentDataList.isEmpty())
            return new Error("Provided Source Attachment Data List was empty!", true);

        // todo: implement attachment ciphering/deciphering logic..



        resultAttachmentDataListWrapper.setValue(sourceAttachmentDataList);

        return null;
    }
}
