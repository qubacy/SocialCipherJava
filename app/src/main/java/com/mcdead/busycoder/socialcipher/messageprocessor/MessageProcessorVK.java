package com.mcdead.busycoder.socialcipher.messageprocessor;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

import java.util.ArrayList;
import java.util.List;

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

            if (attachmentEntity == null)


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
            case IMAGE: return null;
            case DOC: return null;
            case AUDIO: return null;
            case VIDEO: return null;
        }

        return null;
    }
}
