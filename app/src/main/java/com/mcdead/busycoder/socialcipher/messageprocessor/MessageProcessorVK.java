package com.mcdead.busycoder.socialcipher.messageprocessor;

import android.webkit.URLUtil;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentDoc;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.document.ResponseDocumentItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoSize;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.data.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmentdata.AttachmentData;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

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
    public MessageEntity processReceivedMessage(ResponseMessageInterface message,
                                                final long peerId)
    {
        if (message == null) return null;
        if (peerId == 0) return null;

        ResponseDialogItem messageVK = (ResponseDialogItem) message;
        List<AttachmentEntityBase> attachmentList = processReceivedAttachments(messageVK.attachments);

        MessageEntity messageEntity = new MessageEntity(
                messageVK.id,
                messageVK.fromId,
                messageVK.text,
                messageVK.timestamp,
                attachmentList);

        return messageEntity;
    }

    @Override
    public MessageEntity processReceivedUpdateMessage(ResponseUpdateItemInterface update,
                                                      final long peerId)
    {
        if (update == null) return null;
        if (peerId == 0) return null;

        ResponseUpdateItem updateVK = (ResponseUpdateItem) update;
        List<AttachmentEntityBase> attachmentList = processReceivedAttachments(updateVK.attachments);

        MessageEntity messageEntity = new MessageEntity(
                updateVK.messageId,
                updateVK.fromPeerId,
                updateVK.text,
                updateVK.timestamp,
                attachmentList);

        return messageEntity;
    }

    private List<AttachmentEntityBase> processReceivedAttachments(List<ResponseAttachmentBase> attachments)
    {
        if (attachments == null) return null;

        AttachmentTypeDefinerVK attachmentTypeDefiner = (AttachmentTypeDefinerVK) AttachmentTypeDefinerFactory.generateAttachmentTypeDefiner();

        if (attachmentTypeDefiner == null) return null;

        List<AttachmentEntityBase> attachmentEntityList = new ArrayList<>();

        for (final ResponseAttachmentBase attachment : attachments) {
            AttachmentEntityBase attachmentEntity = loadAttachment(attachment);

            if (attachmentEntity != null) {
                attachmentEntityList.add(attachmentEntity);

                continue;
            }

            attachmentEntity = downloadAttachment(attachment);

            if (attachmentEntity == null) {
                // todo: process downloading error..

                continue;
            }

            attachmentEntityList.add(attachmentEntity);
        }

        return attachmentEntityList;
    }

    private AttachmentEntityBase loadAttachment(
            final ResponseAttachmentBase attachmentToDownload)
    {
        if (!(attachmentToDownload instanceof ResponseAttachmentStored))
            return null;

        ResponseAttachmentStored attachmentStored = (ResponseAttachmentStored) attachmentToDownload;
        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance();

        if (attachmentsStore == null) return null;

        return attachmentsStore.getAttachmentById(attachmentStored.getTypedAttachmentID());
    }

    private AttachmentEntityBase downloadAttachment(
            final ResponseAttachmentBase attachmentToDownload)
    {
        AttachmentType attachmentType = m_attachmentTypeDefiner.defineAttachmentTypeByString(
                attachmentToDownload.attachmentType);

        switch (attachmentToDownload.getAttachmentType()) {
            case STORED: return downloadStoredAttachment((ResponseAttachmentStored) attachmentToDownload, attachmentType);
            case LINKED: return downloadLinkedAttachment((ResponseAttachmentLinked) attachmentToDownload, attachmentType);
        }

        return null;
    }

    private AttachmentEntityBase downloadStoredAttachment(
            final ResponseAttachmentStored attachmentToDownload,
            final AttachmentType attachmentType)
    {
        VKAPIInterface vkAPI = APIStore.getAPIInstance();

        if (vkAPI == null) return null;

        switch (attachmentType) {
            case IMAGE: return downloadStoredAttachmentImage(vkAPI, attachmentType, attachmentToDownload);
            case DOC: return downloadStoredAttachmentDoc(vkAPI, attachmentType, attachmentToDownload);
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return null;
    }

    private AttachmentEntityBase downloadStoredAttachmentImage(
            final VKAPIInterface vkAPI,
            final AttachmentType attachmentType,
            final ResponseAttachmentStored attachmentToDownload)
    {
        ResponsePhotoItem responsePhotoItem = null;

        try {
            retrofit2.Response<ResponsePhotoWrapper> responsePhotoWrapper
                    = vkAPI.photo(m_token, attachmentToDownload.attachmentID).execute();

            if (!responsePhotoWrapper.isSuccessful())
                return null;

            // todo: reckon of this poor design sign:

            if (responsePhotoWrapper.body().error != null)
                return null;

            if (responsePhotoWrapper.body().response == null)
                return null;
            if (responsePhotoWrapper.body().response.isEmpty())
                return null;

            responsePhotoItem = responsePhotoWrapper.body().response.get(0);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        if (responsePhotoItem.sizes == null)
            return null;
        if (responsePhotoItem.sizes.isEmpty())
            return null;

        String lastSizeUrl = responsePhotoItem.sizes.get(responsePhotoItem.sizes.size() - 1).url;

        return downloadLinkedAttachmentDefault(
                new ResponseAttachmentLinked(
                        attachmentToDownload.attachmentType,
                        attachmentToDownload.attachmentID,
                        lastSizeUrl),
                attachmentType);
    }

    private AttachmentEntityBase downloadStoredAttachmentDoc(
            final VKAPIInterface vkAPI,
            final AttachmentType attachmentType,
            final ResponseAttachmentStored attachmentToDownload)
    {
        ResponseDocumentItem responseDocItem = null;

        try {
            retrofit2.Response<ResponseDocumentWrapper> responseDocWrapper
                    = vkAPI.document(m_token, attachmentToDownload.attachmentID).execute();

            if (!responseDocWrapper.isSuccessful())
                return null;

            // todo: reckon of this poor design sign:

            if (responseDocWrapper.body().error != null)
                return null;

            if (responseDocWrapper.body().response == null)
                return null;
            if (responseDocWrapper.body().response.isEmpty())
                return null;

            responseDocItem = responseDocWrapper.body().response.get(0);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        return downloadLinkedAttachmentDefault(
                new ResponseAttachmentDoc(
                        attachmentToDownload.attachmentType,
                        attachmentToDownload.attachmentID,
                        responseDocItem.url,
                        responseDocItem.ext),
                attachmentType);
    }

    private AttachmentEntityBase downloadLinkedAttachment(
            final ResponseAttachmentLinked attachmentToDownload,
            final AttachmentType attachmentType)
    {
        switch (attachmentType) {
            case IMAGE:
            case DOC: return downloadLinkedAttachmentDefault(attachmentToDownload, attachmentType);
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return null;
    }

    private AttachmentEntityBase downloadLinkedAttachmentDefault(
            final ResponseAttachmentLinked attachmentToDownload,
            final AttachmentType attachmentType)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url(attachmentToDownload.url)
                .build();
        byte[] attachmentBytes = null;

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) return null;

            attachmentBytes = response.body().bytes();

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        AttachmentData attachmentData = generateAttachmentData(
                attachmentType,
                attachmentToDownload,
                attachmentBytes);

        if (attachmentData == null) return null;

        return generateAttachmentEntity(attachmentData);
    }

    private AttachmentData generateAttachmentData(
            final AttachmentType attachmentType,
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes)
    {
        switch (attachmentType) {
            case IMAGE: return generateAttachmentDataImage(attachmentToDownload, attachmentBytes);
            case DOC: return generateAttachmentDataDoc(attachmentToDownload, attachmentBytes);
        }

        return null;
    }

    private AttachmentData generateAttachmentDataImage(
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes)
    {
        String fileExtension = extractExtensionByUrl(attachmentToDownload.url);

        if (fileExtension.isEmpty()) return null;

        return new AttachmentData(
                attachmentToDownload.getTypedAttachmentID(),
                fileExtension,
                attachmentBytes);
    }

    private AttachmentData generateAttachmentDataDoc(
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes)
    {
        ResponseAttachmentDoc attachmentDoc = (ResponseAttachmentDoc) attachmentToDownload;

        return new AttachmentData(
                attachmentDoc.getTypedAttachmentID(),
                attachmentDoc.ext,
                attachmentBytes);
    }

    private AttachmentEntityBase generateAttachmentEntity(
            final AttachmentData attachmentData)
    {
        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance();

        if (attachmentsStore == null) return null;

        return attachmentsStore.saveAttachment(attachmentData);
    }

    private String extractExtensionByUrl(final String url) {
        // todo: test it enough:

        String fileName = URLUtil.guessFileName(url, null, null);

        return AttachmentContext.getExtensionByFileName(fileName);
    }
}
