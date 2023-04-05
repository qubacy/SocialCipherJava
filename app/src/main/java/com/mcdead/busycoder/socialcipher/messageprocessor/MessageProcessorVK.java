package com.mcdead.busycoder.socialcipher.messageprocessor;

import android.webkit.URLUtil;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentDoc;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.data.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityGenerator;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmentdata.AttachmentData;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

import java.io.IOException;
import java.net.URL;
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

        // todo: loading ATTACHMENTS with storing them
        // todo: in a storage...

        for (final ResponseAttachmentBase attachment : attachments) {
            AttachmentEntityBase attachmentEntity = downloadAttachment(attachment);

            if (attachmentEntity == null) {
                // todo: process downloading error..

                continue;
            }

            attachmentEntityList.add(attachmentEntity);
        }

        return attachmentEntityList;
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
        switch (attachmentType) {
            case IMAGE: return null;
            case DOC: return null;
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return null;
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
        // todo: download an attachment content..

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

        // todo: save grasped bytes to a file..

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
        String photoFileName = extractExtensionByUrl(attachmentToDownload.url);

        if (photoFileName.isEmpty()) return null;

        String fileName = AttachmentContext.getAttachmentIdByFileName(photoFileName);
        String fileExtension = AttachmentContext.getExtensionByFileName(photoFileName);

        return new AttachmentData(fileName, fileExtension, attachmentBytes);
    }

    private AttachmentData generateAttachmentDataDoc(
            final ResponseAttachmentLinked attachmentToDownload,
            final byte[] attachmentBytes)
    {
        ResponseAttachmentDoc attachmentDoc = (ResponseAttachmentDoc) attachmentToDownload;

        return new AttachmentData(
                attachmentDoc.attachmentID, attachmentDoc.ext,
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

        return URLUtil.guessFileName(url, null, null);
    }
}
