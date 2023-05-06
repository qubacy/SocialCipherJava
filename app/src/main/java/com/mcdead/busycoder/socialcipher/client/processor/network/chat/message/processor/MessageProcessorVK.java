package com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor;

import android.util.Pair;
import android.webkit.URLUtil;

import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentDoc;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentLinked;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.list.ResponseChatAttachmentListItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.list.ResponseChatAttachmentListWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.document.ResponseDocumentItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.photo.ResponsePhotoItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.cipher.MessageCipherProcessor;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
*
* NOTE: should be used ONLY in BACKGROUND!
* generates MessageEntities from raw responses;
*
*/
public class MessageProcessorVK extends MessageProcessorBase {
    public MessageProcessorVK(AttachmentTypeDefinerInterface attachmentTypeDefiner,
                              String token)
    {
        super(attachmentTypeDefiner, token);
    }

    @Override
    public Error processReceivedMessage(
            final ResponseMessageInterface message,
            final long peerId,
            ObjectWrapper<MessageEntity> resultMessage)
    {
        if (message == null)
            return new Error("Update hasn't been initialized!", true);
        if (peerId == 0)
            return new Error("Invalid Peer Id has been provided!", true);
        if (resultMessage == null)
            return new Error("Result Message Wrapper hasn't been initialized!", true);

        ResponseChatContentItem messageVK = (ResponseChatContentItem) message;
        List<ResponseAttachmentInterface> attachmentsToLoadList =
                (messageVK.attachments == null
                ? null
                : new ArrayList<ResponseAttachmentInterface>(messageVK.attachments));

        MessageEntity messageEntity = MessageEntityGenerator.generateMessage(
                messageVK.id,
                messageVK.fromId,
                messageVK.text.replace("<br>", "\n"),
                messageVK.timestamp,
                false,
                attachmentsToLoadList);

        if (messageEntity == null)
            return new Error("New message generation process went wrong!", true);

        resultMessage.setValue(messageEntity);

        return null;
    }

    @Override
    public Error processReceivedUpdateMessage(
            final ResponseUpdateItemInterface update,
            final long peerId,
            ObjectWrapper<MessageEntity> resultMessage)
    {
        if (update == null)
            return new Error("Update hasn't been initialized!", true);
        if (peerId == 0)
            return new Error("Invalid Peer Id has been provided!", true);
        if (resultMessage == null)
            return new Error("Result Message Wrapper hasn't been initialized!", true);

        ResponseUpdateItem updateVK = (ResponseUpdateItem) update;
        List<ResponseAttachmentInterface> attachmentsToLoadList =
                (updateVK.attachments == null
                        ? null
                        : new ArrayList<ResponseAttachmentInterface>(updateVK.attachments));

        MessageCipherProcessor messageCipherProcessor =
                MessageCipherProcessor.getInstance(peerId);
        String processedMessageText = updateVK.text;
        boolean isCiphered = false;

        if (messageCipherProcessor != null) {
            Error cipheringError;

            if (!updateVK.text.isEmpty()) {
                ObjectWrapper<Pair<Boolean, String>> processedSuccessFlagText = new ObjectWrapper<>();
                cipheringError = messageCipherProcessor.processText(
                        updateVK.text, false, processedSuccessFlagText);

                if (cipheringError != null)
                    return cipheringError;
                if (processedSuccessFlagText.getValue().first)
                    isCiphered = true;

                processedMessageText = processedSuccessFlagText.getValue().second;
            }
        }

        MessageEntity messageEntity = MessageEntityGenerator.generateMessage(
                updateVK.messageId,
                updateVK.fromPeerId,
                processedMessageText.replace("<br>", "\n"),
                updateVK.timestamp,
                isCiphered,
                attachmentsToLoadList);

        if (messageEntity == null)
            return new Error("New message generation process went wrong!", true);

        resultMessage.setValue(messageEntity);

        return null;
    }

    @Override
    public Error processMessageAttachments(
            final MessageEntity message,
            final long chatId)
    {
        if (message == null)
            return new Error("Message hasn't been initialized!", true);
        if (message.getAttachmentToLoad() == null)
            return null;

        List<AttachmentEntityBase> loadedAttachments = new ArrayList<>();
        boolean icCiphered = false;

        for (final ResponseAttachmentInterface attachmentToLoad : message.getAttachmentToLoad()) {
            if (attachmentToLoad == null) continue;

            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper =
                    new ObjectWrapper<>(new Pair<>(null, false));
            Error err = processAttachment(attachmentToLoad, chatId, attachmentEntityCipheredFlagWrapper);

            if (err != null) return err;

            loadedAttachments.add(attachmentEntityCipheredFlagWrapper.getValue().first);

            if (attachmentEntityCipheredFlagWrapper.getValue().second && !icCiphered)
                icCiphered = true;
        }

        if (loadedAttachments.isEmpty()) return null;

        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);

        if (!dialogsStore.setMessageAttachments(loadedAttachments, chatId, message.getId(), icCiphered))
            return new Error(
                "Setting attachments to message process went wrong!",
                true);

        return null;
    }

    private Error processAttachment(
            ResponseAttachmentInterface attachmentToLoad,
            final long chatId,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        ResponseAttachmentBase attachmentVK = (ResponseAttachmentBase) attachmentToLoad;

        if (attachmentVK == null)
            return new Error("Raw attachment had a wrong type!", true);

        AttachmentTypeDefinerVK attachmentTypeDefiner =
                (AttachmentTypeDefinerVK) AttachmentTypeDefinerFactory.generateAttachmentTypeDefiner();

        if (attachmentTypeDefiner == null)
            return new Error("AttachmentTypeDefiner hasn't been initialized!", true);

        ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper = new ObjectWrapper<>();
        Error loadAttachmentError = loadAttachment(attachmentVK, attachmentEntityWrapper);

        if (loadAttachmentError != null)
            return loadAttachmentError;
        if (attachmentEntityWrapper.getValue() != null) {
            attachmentEntityCipheredFlagWrapper.setValue(new Pair<>(attachmentEntityWrapper.getValue(), false));

            return null;
        }

        ObjectWrapper<ResponseAttachmentBase> preparedToDownloadAttachmentWrapper =
                new ObjectWrapper<>();
        Error prepareToDownloadError = prepareAttachmentToDownload(
                attachmentVK, chatId, preparedToDownloadAttachmentWrapper);

        if (prepareToDownloadError != null)
            return prepareToDownloadError;

        attachmentVK = preparedToDownloadAttachmentWrapper.getValue();

        Error downloadAttachmentError =
                downloadAttachment(attachmentVK, chatId, attachmentEntityCipheredFlagWrapper);

        if (downloadAttachmentError != null)
            return downloadAttachmentError;

        return null;
    }

    private Error loadAttachment(
            final ResponseAttachmentBase attachmentToDownload,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        if (!(attachmentToDownload instanceof ResponseAttachmentStored))
            return new Error("Provided Attachment wasn't an instance of Stored Attachment type!", true);

        ResponseAttachmentStored attachmentStored = (ResponseAttachmentStored) attachmentToDownload;
        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance();

        if (attachmentsStore == null)
            return new Error("Attachment Store hasn't been initialized!", true);

        AttachmentEntityBase attachmentEntity =
                attachmentsStore.getAttachmentById(attachmentStored.getTypedShortAttachmentId());

        if (attachmentEntity == null)
            return null;

        attachmentEntityWrapper.setValue(attachmentEntity);

        return null;
    }

    private Error prepareAttachmentToDownload(
            final ResponseAttachmentBase attachmentToPrepare,
            final long charId,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        AttachmentType attachmentType = m_attachmentTypeDefiner.defineAttachmentTypeByString(
                attachmentToPrepare.getAttachmentType());

        switch (attachmentToPrepare.getResponseAttachmentType()) {
            case STORED: return prepareStoredAttachmentToDownload((ResponseAttachmentStored) attachmentToPrepare, charId, attachmentType, preparedAttachmentWrapper);
            case LINKED: return prepareLinkedAttachmentToDownload((ResponseAttachmentLinked) attachmentToPrepare, charId, attachmentType, preparedAttachmentWrapper);
        }

        return new Error("Preparing for a provided Attachment process cannot be executed on a provided attachment!", true);
    }

    private Error prepareStoredAttachmentToDownload(
            final ResponseAttachmentStored attachmentToPrepare,
            final long chatId,
            final AttachmentType attachmentType,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new Error("API hasn't been initialized!", true);

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        switch (attachmentType) {
            case IMAGE:
            case DOC: return prepareStoredAttachmentDefaultToDownload(
                    vkAPIChat,
                    attachmentToPrepare,
                    chatId,
                    preparedAttachmentWrapper);
//            case AUDIO:
//            case VIDEO: return null;
        }

        return new Error(
                "Preparing for a provided Stored Attachment process cannot be executed on a provided attachment!",
                true);
    }

    private Error prepareLinkedAttachmentToDownload(
            ResponseAttachmentLinked attachmentToPrepare,
            final long chatId,
            final AttachmentType attachmentType,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        // nothing to do for now..

        preparedAttachmentWrapper.setValue(attachmentToPrepare);

        return null;
    }

    private Error prepareStoredAttachmentDefaultToDownload(
            final VKAPIChat vkAPIChat,
            final ResponseAttachmentStored attachmentToPrepare,
            final long chatId,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        List<ResponseChatAttachmentListItem> attachmentItemList = null;

        try {
            retrofit2.Response<ResponseChatAttachmentListWrapper> response =
                    vkAPIChat.getChatAttachmentList(chatId, attachmentToPrepare.getAttachmentType(), m_token)
                            .execute();

            if (!response.isSuccessful())
                return new Error("Chat Attachments request has been failed!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            attachmentItemList = response.body().response.items;

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        if (attachmentItemList == null)
            return new Error("Retrieved Chat Attachments list was null!", true);

        for (final ResponseChatAttachmentListItem attachmentItem : attachmentItemList) {
            ResponseAttachmentStored attachmentItemData =
                    (ResponseAttachmentStored) attachmentItem.attachment;

            if (attachmentItemData == null) continue;

            if (attachmentItemData.getAttachmentType()
               .compareTo(attachmentToPrepare.getAttachmentType()) != 0)
            {
                continue;
            }

            if (attachmentItemData.getAttachmentId()
             != attachmentToPrepare.getAttachmentId())
            {
                continue;
            }

            if (attachmentItemData.getAttachmentOwnerId()
             != attachmentToPrepare.getAttachmentOwnerId())
            {
                continue;
            }

            preparedAttachmentWrapper.setValue(attachmentItemData);

            break;
        }

        return null;
    }

    private Error downloadAttachment(
            final ResponseAttachmentBase attachmentToDownload,
            final long chatId,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        AttachmentType attachmentType = m_attachmentTypeDefiner.defineAttachmentTypeByString(
                attachmentToDownload.getAttachmentType());

        switch (attachmentToDownload.getResponseAttachmentType()) {
            case STORED: return downloadStoredAttachment((ResponseAttachmentStored) attachmentToDownload, attachmentType, attachmentEntityCipheredFlagWrapper);
            case LINKED: return downloadLinkedAttachment((ResponseAttachmentLinked) attachmentToDownload, attachmentType, chatId, attachmentEntityCipheredFlagWrapper);
        }

        return new Error("Downloading Attachment operation cannot be executed on a provided attachment!", true);
    }

    private Error downloadStoredAttachment(
            final ResponseAttachmentStored attachmentToDownload,
            final AttachmentType attachmentType,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new Error("API hasn't been initialized!", true);

        VKAPIAttachment vkAPIAttachment = vkAPIProvider.generateAttachmentAPI();

        switch (attachmentType) {
            case IMAGE: return downloadStoredAttachmentImage(
                    vkAPIAttachment,
                    attachmentType,
                    attachmentToDownload,
                    attachmentEntityCipheredFlagWrapper);
            case DOC: return downloadStoredAttachmentDoc(
                    vkAPIAttachment,
                    attachmentType,
                    attachmentToDownload,
                    attachmentEntityCipheredFlagWrapper);
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return new Error("Downloading Stored Attachment operation cannot be executed on a provided attachment!", true);
    }

    private Error downloadStoredAttachmentImage(
            final VKAPIAttachment vkAPIAttachment,
            final AttachmentType attachmentType,
            final ResponseAttachmentStored attachmentToDownload,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        ResponsePhotoItem responsePhotoItem = null;

        try {
            retrofit2.Response<ResponsePhotoWrapper> responsePhotoWrapper
                    = vkAPIAttachment.getPhoto(
                            m_token,
                            attachmentToDownload.getFullAttachmentId()).execute();

            if (!responsePhotoWrapper.isSuccessful())
                return new Error("Getting Photo Attachment Data response process has been failed!", true);

            // todo: reckon of this poor design sign:

            if (responsePhotoWrapper.body().error != null)
                return new Error(responsePhotoWrapper.body().error.message, true);

            if (responsePhotoWrapper.body().response == null)
                return new Error("Getting Photo Attachment Data response process ended with a null response body!", true);
            if (responsePhotoWrapper.body().response.isEmpty())
                return new Error("Getting Photo Attachment Data response process ended with an empty response body!", true);

            responsePhotoItem = responsePhotoWrapper.body().response.get(0);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        if (responsePhotoItem.sizes == null)
            return new Error("Received array of photo sizes was null!", true);
        if (responsePhotoItem.sizes.isEmpty())
            return new Error("Received array of photo sizes was empty!", true);

        HashMap<AttachmentSize, String> attachmentSizeUrlHashMap =
                new HashMap<>();

        String smallSizeUrl = responsePhotoItem.sizes.get(0).url;
        String lastSizeUrl = responsePhotoItem.sizes.get(responsePhotoItem.sizes.size() - 1).url;

        attachmentSizeUrlHashMap.put(AttachmentSize.SMALL, smallSizeUrl);
        attachmentSizeUrlHashMap.put(AttachmentSize.STANDARD, lastSizeUrl);

        ResponseAttachmentLinked attachmentLinked =
                ResponseAttachmentLinked.generateAttachmentLinkedWithFullAttachmentId(
                        attachmentToDownload.getAttachmentType(),
                        attachmentToDownload.getFullAttachmentId(),
                        attachmentSizeUrlHashMap
                );

        return downloadLinkedAttachmentDefault(
                attachmentLinked,
                attachmentType,
                attachmentEntityCipheredFlagWrapper);
    }

    private Error downloadStoredAttachmentDoc(
            final VKAPIAttachment vkAPIAttachment,
            final AttachmentType attachmentType,
            final ResponseAttachmentStored attachmentToDownload,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        ResponseDocumentItem responseDocItem = null;

        try {
            retrofit2.Response<ResponseDocumentWrapper> responseDocWrapper
                    = vkAPIAttachment.getDocument(
                            m_token,
                            attachmentToDownload.getFullAttachmentId()).execute();

            if (!responseDocWrapper.isSuccessful())
                return new Error("Requesting Doc Link process ended with a failed request!", true);

            // todo: reckon of this poor design sign:

            if (responseDocWrapper.body().error != null)
                return new Error(responseDocWrapper.body().error.message, true);

            if (responseDocWrapper.body().response == null)
                return new Error("Requesting Doc Link process ended with an empty response part!", true);
            if (responseDocWrapper.body().response.isEmpty())
                return new Error("Requesting Doc Link process ended with an empty response body!", true);

            responseDocItem = responseDocWrapper.body().response.get(0);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        HashMap<AttachmentSize, String> attachmentSizeUrlHashMap =
                new HashMap<>();

        attachmentSizeUrlHashMap.put(AttachmentSize.STANDARD, responseDocItem.url);

        ResponseAttachmentDoc responseAttachmentDoc =
                ResponseAttachmentDoc.generateAttachmentDocWithFullAttachmentId(
                    attachmentToDownload.getAttachmentType(),
                    attachmentToDownload.getFullAttachmentId(),
                    attachmentSizeUrlHashMap,
                    responseDocItem.ext);

        return downloadLinkedAttachmentDefault(
                responseAttachmentDoc,
                attachmentType,
                attachmentEntityCipheredFlagWrapper);
    }

    private Error downloadLinkedAttachment(
            final ResponseAttachmentLinked attachmentLink,
            final AttachmentType attachmentType,
            final long chatId,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        MessageCipherProcessor messageCipherProcessor =
                MessageCipherProcessor.getInstance(chatId);

        if (messageCipherProcessor != null && attachmentType == AttachmentType.DOC) {
            ResponseAttachmentDoc attachmentDoc = (ResponseAttachmentDoc) attachmentLink;
            ObjectWrapper<Boolean> successFlagWrapper = new ObjectWrapper<>();

            if (attachmentDoc.getExtension().compareTo(MessageCipherProcessor.C_CIPHERED_ATTACHMENT_EXT) == 0) {
                Error downloadingCipheredError =
                        downloadCipheredAttachment(
                            messageCipherProcessor,
                            attachmentDoc,
                            successFlagWrapper,
                                attachmentEntityCipheredFlagWrapper);

                if (downloadingCipheredError != null)
                    return downloadingCipheredError;

                if (successFlagWrapper.getValue())
                    return null;
            }
        }

        switch (attachmentType) {
            case IMAGE:
            case DOC: return downloadLinkedAttachmentDefault(attachmentLink, attachmentType, attachmentEntityCipheredFlagWrapper);
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return new Error(
                "Downloading cannot be processed on a provided attachment!",
                true);
    }

    private Error downloadCipheredAttachment(
            final MessageCipherProcessor cipherProcessor,
            final ResponseAttachmentDoc attachmentDoc,
            ObjectWrapper<Boolean> successFlagWrapper,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        ObjectWrapper<byte[]> downloadedBytesWrapper = new ObjectWrapper<>();
        Error downloadingError =
                downloadFile(
                        okHttpClient,
                        attachmentDoc.getUrlBySize(AttachmentSize.STANDARD),
                        downloadedBytesWrapper);

        if (downloadingError != null)
            return downloadingError;

        // todo: deciphering bytes...

        ObjectWrapper<MessageCipherProcessor.AttachmentDecipheringResult> attachmentDecipheringResultWrapper =
                new ObjectWrapper<>();
        Error decipheringError =
                decipherAttachment(
                        cipherProcessor,
                        downloadedBytesWrapper.getValue(),
                        attachmentDecipheringResultWrapper);

        if (decipheringError != null)
            return decipheringError;

        if (!attachmentDecipheringResultWrapper.getValue().isSuccessful()) {
            successFlagWrapper.setValue(false);

            return null;
        }

        successFlagWrapper.setValue(true);

        ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper = new ObjectWrapper<>();
        Error generatingError =
             generateDecipheredAttachmentEntity(
                 new AttachmentData(
                         attachmentDoc.getShortAttachmentId(),
                         attachmentDecipheringResultWrapper.getValue().getFileExtension(),
                         attachmentDecipheringResultWrapper.getValue().getBytes()),
                     attachmentEntityWrapper);

        if (generatingError != null)
            return generatingError;

        attachmentEntityCipheredFlagWrapper.setValue(
                new Pair<>(attachmentEntityWrapper.getValue(), true));

        return null;
    }

    private Error downloadLinkedAttachmentDefault(
            final ResponseAttachmentLinked attachmentLinked,
            final AttachmentType attachmentType,
            ObjectWrapper<Pair<AttachmentEntityBase, Boolean>> attachmentEntityCipheredFlagWrapper)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        HashMap<AttachmentSize, byte[]> attachmentSizeBytesHashMap = new HashMap<>();

        for (final Map.Entry<AttachmentSize, String> attachmentSizeLink :
                attachmentLinked.getSizeUrlHashMap().entrySet())
        {
            ObjectWrapper<byte[]> downloadedBytesWrapper = new ObjectWrapper<>();
            Error downloadingError =
                    downloadFile(
                            okHttpClient, attachmentSizeLink.getValue(), downloadedBytesWrapper);

            if (downloadingError != null)
                return downloadingError;

            attachmentSizeBytesHashMap.put(attachmentSizeLink.getKey(), downloadedBytesWrapper.getValue());
        }

        ObjectWrapper<HashMap<AttachmentSize, AttachmentData>> attachmentSizeDataHashMapWrapper =
                new ObjectWrapper<>();
        Error attachmentDataError = generateAttachmentSizeDataHashMap(
                attachmentType,
                attachmentLinked,
                attachmentSizeBytesHashMap,
                attachmentSizeDataHashMapWrapper);

        if (attachmentDataError != null)
            return attachmentDataError;

        ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper = new ObjectWrapper<>();
        Error generatingError =
                generateAttachmentEntity(
                    attachmentSizeDataHashMapWrapper.getValue(),
                        attachmentEntityWrapper);

        if (generatingError != null)
            return generatingError;

        attachmentEntityCipheredFlagWrapper.setValue(
                new Pair<>(attachmentEntityWrapper.getValue(), false));

        return null;
    }

    private Error downloadFile(
            final OkHttpClient okHttpClient,
            final String url,
            ObjectWrapper<byte[]> downloadedBytesWrapper)
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful())
                return new Error("Downloading Attachment process has been failed!", true);

            downloadedBytesWrapper.setValue(response.body().bytes());

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        return null;
    }

    private Error decipherAttachment(
            final MessageCipherProcessor messageCipherProcessor,
            final byte[] sourceBytes,
            ObjectWrapper<MessageCipherProcessor.AttachmentDecipheringResult> attachmentDecipheringResultWrapper)
    {
        return messageCipherProcessor.decipherAttachmentBytes(
                sourceBytes, attachmentDecipheringResultWrapper);
    }

    private Error generateAttachmentSizeDataHashMap(
            final AttachmentType attachmentType,
            final ResponseAttachmentLinked attachmentLink,
            final HashMap<AttachmentSize, byte[]> attachmentSizeBytesHashMap,
            ObjectWrapper<HashMap<AttachmentSize, AttachmentData>> attachmentSizeDataHashMapWrapper)
    {
        switch (attachmentType) {
            case IMAGE: return generateAttachmentSizeDataImageHashMap(
                    attachmentLink,
                    attachmentSizeBytesHashMap,
                    attachmentSizeDataHashMapWrapper);
            case DOC: return generateAttachmentDataDoc(
                    attachmentLink,
                    attachmentSizeBytesHashMap,
                    attachmentSizeDataHashMapWrapper);
        }

        return null;
    }

    private Error generateAttachmentSizeDataImageHashMap(
            final ResponseAttachmentLinked attachmentLink,
            final HashMap<AttachmentSize, byte[]> attachmentSizeBytesHashMap,
            ObjectWrapper<HashMap<AttachmentSize, AttachmentData>> attachmentSizeDataHashMapWrapper)
    {
        String fileExtension = extractExtensionByUrl(attachmentLink.getUrlBySize(AttachmentSize.STANDARD));

        if (fileExtension.isEmpty())
            return new Error("Attachment File Extension was empty!", true);

        HashMap<AttachmentSize, AttachmentData> attachmentSizeDataHashMap  = new HashMap<>();

        for (final Map.Entry<AttachmentSize, byte[]> attachmentSizeBytes :
                attachmentSizeBytesHashMap.entrySet())
        {
            AttachmentData attachmentSizeData = new AttachmentData(
                    attachmentLink.getTypedShortAttachmentId(),
                    fileExtension,
                    attachmentSizeBytes.getValue());

            attachmentSizeDataHashMap.put(attachmentSizeBytes.getKey(), attachmentSizeData);
        }

        attachmentSizeDataHashMapWrapper.setValue(attachmentSizeDataHashMap);

        return null;
    }

    private Error generateAttachmentDataDoc(
            final ResponseAttachmentLinked attachmentLink,
            final HashMap<AttachmentSize, byte[]> attachmentSizeBytesHashMap,
            ObjectWrapper<HashMap<AttachmentSize, AttachmentData>> attachmentSizeDataHashMapWrapper)
    {
        ResponseAttachmentDoc attachmentDoc = (ResponseAttachmentDoc) attachmentLink;
        HashMap<AttachmentSize, AttachmentData> attachmentSizeDataHashMap  = new HashMap<>();

        for (final Map.Entry<AttachmentSize, byte[]> attachmentSizeBytes :
                attachmentSizeBytesHashMap.entrySet())
        {
            AttachmentData attachmentSizeData = new AttachmentData(
                    attachmentDoc.getTypedShortAttachmentId(),
                    attachmentDoc.getExtension(),
                    attachmentSizeBytes.getValue());

            attachmentSizeDataHashMap.put(attachmentSizeBytes.getKey(), attachmentSizeData);
        }

        attachmentSizeDataHashMapWrapper.setValue(attachmentSizeDataHashMap);

        return null;
    }

    private Error generateDecipheredAttachmentEntity(
            final AttachmentData attachmentData,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance();

        if (attachmentsStore == null)
            return new Error("Attachment Store hasn't been initialized!", true);

        attachmentEntityWrapper.setValue(attachmentsStore.saveCachedAttachment(attachmentData));

        return null;
    }

    private Error generateAttachmentEntity(
            final HashMap<AttachmentSize, AttachmentData> attachmentSizeDataHashMap,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance();

        if (attachmentsStore == null)
            return new Error("Attachment Store hasn't been initialized!", true);

        attachmentEntityWrapper.setValue(attachmentsStore.saveAttachment(attachmentSizeDataHashMap));

        return null;
    }

    private String extractExtensionByUrl(final String url) {
        // todo: test it enough:

        String fileName = URLUtil.guessFileName(url, null, null);

        return AttachmentContext.getExtensionByFileName(fileName);
    }
}
