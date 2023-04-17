package com.mcdead.busycoder.socialcipher.messageprocessor;

import android.webkit.URLUtil;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentDoc;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.chat.ResponseChatAttachmentsItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.chat.ResponseChatAttachmentsWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.document.ResponseDocumentItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.data.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmentdata.AttachmentData;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        ResponseDialogItem messageVK = (ResponseDialogItem) message;
        List<ResponseAttachmentInterface> attachmentsToLoadList =
                (messageVK.attachments == null
                ? null
                : new ArrayList<ResponseAttachmentInterface>(messageVK.attachments));

        MessageEntity messageEntity = new MessageEntity(
                messageVK.id,
                messageVK.fromId,
                messageVK.text,
                messageVK.timestamp,
                attachmentsToLoadList);

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

        MessageEntity messageEntity = new MessageEntity(
                updateVK.messageId,
                updateVK.fromPeerId,
                updateVK.text,
                updateVK.timestamp,
                attachmentsToLoadList);

        resultMessage.setValue(messageEntity);

        return null;
    }

    @Override
    public Error processMessageAttachments(
            final MessageEntity message,
            final long charId)
    {
        if (message == null)
            return new Error("Message hasn't been initialized!", true);
        if (message.getAttachmentToLoad() == null)
            return null;

        List<AttachmentEntityBase> loadedAttachments = new ArrayList<>();

        for (final ResponseAttachmentInterface attachmentToLoad : message.getAttachmentToLoad()) {
            if (attachmentToLoad == null) continue;

            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper = new ObjectWrapper<>();
            Error err = processAttachment(attachmentToLoad, charId, attachmentEntityWrapper);

            if (err != null) return err;

            loadedAttachments.add(attachmentEntityWrapper.getValue());
        }

        if (loadedAttachments.isEmpty()) return null;

        DialogsStore dialogsStore = DialogsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);

        if (!dialogsStore.setMessageAttachments(loadedAttachments, charId, message.getId()))
            return new Error(
                "Setting attachments to message process went wrong!",
                true);

        return null;
    }

    private Error processAttachment(
            ResponseAttachmentInterface attachmentToLoad,
            final long charId,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        ResponseAttachmentBase attachmentVK = (ResponseAttachmentBase) attachmentToLoad;

        if (attachmentVK == null)
            return new Error("Raw attachment had a wrong type!", true);

        AttachmentTypeDefinerVK attachmentTypeDefiner = (AttachmentTypeDefinerVK) AttachmentTypeDefinerFactory.generateAttachmentTypeDefiner();

        if (attachmentTypeDefiner == null)
            return new Error("AttachmentTypeDefiner hasn't been initialized!", true);

        Error loadAttachmentError = loadAttachment(attachmentVK, attachmentEntityWrapper);

        if (loadAttachmentError != null)
            return loadAttachmentError;
        if (attachmentEntityWrapper.getValue() != null)
            return null;

        ObjectWrapper<ResponseAttachmentBase> preparedToDownloadAttachmentWrapper =
                new ObjectWrapper<>();

        Error prepareToDownloadError = prepareAttachmentToDownload(attachmentVK, charId, preparedToDownloadAttachmentWrapper);

        if (prepareToDownloadError != null)
            return prepareToDownloadError;

        attachmentVK = preparedToDownloadAttachmentWrapper.getValue();

        Error downloadAttachmentError = downloadAttachment(attachmentVK, attachmentEntityWrapper);

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
                attachmentsStore.getAttachmentById(attachmentStored.getTypedFullAttachmentID());

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
            final long charId,
            final AttachmentType attachmentType,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("VKAPI instance hasn't been initialized!", true);

        switch (attachmentType) {
            case IMAGE: return prepareStoredAttachmentImageToDownload(vkAPI, attachmentToPrepare, charId, preparedAttachmentWrapper);
            case DOC: return prepareStoredAttachmentDocToDownload(vkAPI, attachmentToPrepare, charId, preparedAttachmentWrapper);
//            case AUDIO:
//            case VIDEO: return null;
        }

        return new Error("Preparing for a provided Stored Attachment process cannot be executed on a provided attachment!", true);
    }

    private Error prepareLinkedAttachmentToDownload(
            ResponseAttachmentLinked attachmentToPrepare,
            final long charId,
            final AttachmentType attachmentType,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        // nothing to do for now..

        preparedAttachmentWrapper.setValue(attachmentToPrepare);

        return null;
    }

    private Error prepareStoredAttachmentImageToDownload(
            final VKAPIInterface vkAPI,
            final ResponseAttachmentStored attachmentToPrepare,
            final long charId,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        // todo: get a list of attachments for a specified CHAT..

        List<ResponseChatAttachmentsItem> attachmentItemList = null;

        try {
            retrofit2.Response<ResponseChatAttachmentsWrapper> response =
                    vkAPI.getChatAttachments(charId, attachmentToPrepare.getAttachmentType(), m_token)
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

        for (final ResponseChatAttachmentsItem attachmentItem : attachmentItemList) {
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

    private Error prepareStoredAttachmentDocToDownload(
            final VKAPIInterface vkAPI,
            final ResponseAttachmentStored attachmentToPrepare,
            final long charId,
            ObjectWrapper<ResponseAttachmentBase> preparedAttachmentWrapper)
    {
        // nothing to do this time..

        preparedAttachmentWrapper.setValue(attachmentToPrepare);

        return null;
    }

    private Error downloadAttachment(
            final ResponseAttachmentBase attachmentToDownload,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        AttachmentType attachmentType = m_attachmentTypeDefiner.defineAttachmentTypeByString(
                attachmentToDownload.getAttachmentType());

        switch (attachmentToDownload.getResponseAttachmentType()) {
            case STORED: return downloadStoredAttachment((ResponseAttachmentStored) attachmentToDownload, attachmentType, attachmentEntityWrapper);
            case LINKED: return downloadLinkedAttachment((ResponseAttachmentLinked) attachmentToDownload, attachmentType, attachmentEntityWrapper);
        }

        return new Error("Downloading Stored Attachment operation cannot be executed on a provided attachment!", true);
    }

    private Error downloadStoredAttachment(
            final ResponseAttachmentStored attachmentToDownload,
            final AttachmentType attachmentType,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("VKAPI instance hasn't been initialized!", true);

        switch (attachmentType) {
            case IMAGE: return downloadStoredAttachmentImage(vkAPI, attachmentType, attachmentToDownload, attachmentEntityWrapper);
            case DOC: return downloadStoredAttachmentDoc(vkAPI, attachmentType, attachmentToDownload, attachmentEntityWrapper);
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return new Error("Downloading Stored Attachment operation cannot be executed on a provided attachment!", true);
    }

    private Error downloadStoredAttachmentImage(
            final VKAPIInterface vkAPI,
            final AttachmentType attachmentType,
            final ResponseAttachmentStored attachmentToDownload,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        ResponsePhotoItem responsePhotoItem = null;

        try {
            retrofit2.Response<ResponsePhotoWrapper> responsePhotoWrapper
                    = vkAPI.photo(m_token, attachmentToDownload.getFullAttachmentId()).execute();

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

        String lastSizeUrl = responsePhotoItem.sizes.get(responsePhotoItem.sizes.size() - 1).url;

        return downloadLinkedAttachmentDefault(
                ResponseAttachmentLinked.generateAttachmentLinkedWithFullAttachmentId(
                        attachmentToDownload.getAttachmentType(),
                        attachmentToDownload.getFullAttachmentId(),
                        lastSizeUrl),
                attachmentType,
                attachmentEntityWrapper);
    }

    private Error downloadStoredAttachmentDoc(
            final VKAPIInterface vkAPI,
            final AttachmentType attachmentType,
            final ResponseAttachmentStored attachmentToDownload,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        ResponseDocumentItem responseDocItem = null;

        try {
            retrofit2.Response<ResponseDocumentWrapper> responseDocWrapper
                    = vkAPI.document(m_token, attachmentToDownload.getFullAttachmentId()).execute();

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

        return downloadLinkedAttachmentDefault(
                ResponseAttachmentDoc.generateAttachmentDocWithFullAttachmentId(
                        attachmentToDownload.getAttachmentType(),
                        attachmentToDownload.getFullAttachmentId(),
                        responseDocItem.url,
                        responseDocItem.ext),
                attachmentType,
                attachmentEntityWrapper);
    }

    private Error downloadLinkedAttachment(
            final ResponseAttachmentLinked attachmentToDownload,
            final AttachmentType attachmentType,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        switch (attachmentType) {
            case IMAGE:
            case DOC: return downloadLinkedAttachmentDefault(attachmentToDownload, attachmentType, attachmentEntityWrapper);
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return new Error(
                "Downloading cannot be processed on a provided attachment!",
                true);
    }

    private Error downloadLinkedAttachmentDefault(
            final ResponseAttachmentLinked attachmentToDownload,
            final AttachmentType attachmentType,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url(attachmentToDownload.getUrl())
                .build();
        byte[] attachmentBytes = null;

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful())
                return new Error("Downloading Attachment process has been failed!", true);

            attachmentBytes = response.body().bytes();

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        ObjectWrapper<AttachmentData> attachmentDataWrapper =
                new ObjectWrapper<>();

        Error attachmentDataError = generateAttachmentData(
                attachmentType,
                attachmentToDownload,
                attachmentBytes,
                attachmentDataWrapper);

        if (attachmentDataError != null)
            return attachmentDataError;

        return generateAttachmentEntity(
                attachmentDataWrapper.getValue(),
                attachmentEntityWrapper);
    }

    private Error generateAttachmentData(
            final AttachmentType attachmentType,
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes,
            ObjectWrapper<AttachmentData> attachmentDataWrapper)
    {
        switch (attachmentType) {
            case IMAGE: return generateAttachmentDataImage(attachmentToDownload, attachmentBytes, attachmentDataWrapper);
            case DOC: return generateAttachmentDataDoc(attachmentToDownload, attachmentBytes, attachmentDataWrapper);
        }

        return null;
    }

    private Error generateAttachmentDataImage(
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes,
            ObjectWrapper<AttachmentData> attachmentDataWrapper)
    {
        String fileExtension = extractExtensionByUrl(attachmentToDownload.getUrl());

        if (fileExtension.isEmpty())
            return new Error("Attachment File Extension was empty!", true);

        attachmentDataWrapper.setValue(new AttachmentData(
                attachmentToDownload.getTypedFullAttachmentID(),
                fileExtension,
                attachmentBytes));

        return null;
    }

    private Error generateAttachmentDataDoc(
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes,
            ObjectWrapper<AttachmentData> attachmentDataWrapper)
    {
        ResponseAttachmentDoc attachmentDoc = (ResponseAttachmentDoc) attachmentToDownload;

        attachmentDataWrapper.setValue(new AttachmentData(
                attachmentDoc.getTypedFullAttachmentID(),
                attachmentDoc.getExtension(),
                attachmentBytes));

        return null;
    }

    private Error generateAttachmentEntity(
            final AttachmentData attachmentData,
            ObjectWrapper<AttachmentEntityBase> attachmentEntityWrapper)
    {
        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance();

        if (attachmentsStore == null)
            return new Error("Attachment Store hasn't been initialized!", true);

        attachmentEntityWrapper.setValue(attachmentsStore.saveAttachment(attachmentData));

        return null;
    }

    private String extractExtensionByUrl(final String url) {
        // todo: test it enough:

        String fileName = URLUtil.guessFileName(url, null, null);

        return AttachmentContext.getExtensionByFileName(fileName);
    }
}
