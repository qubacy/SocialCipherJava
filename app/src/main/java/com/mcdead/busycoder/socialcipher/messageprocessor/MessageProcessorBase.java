package com.mcdead.busycoder.socialcipher.messageprocessor;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

public abstract class MessageProcessorBase {
    protected AttachmentTypeDefinerInterface m_attachmentTypeDefiner = null;
    protected String m_token = null;

    public MessageProcessorBase(final AttachmentTypeDefinerInterface attachmentTypeDefiner,
                                final String token)
    {
        m_attachmentTypeDefiner = attachmentTypeDefiner;
        m_token = token;
    }

    public abstract MessageEntity processReceivedMessage(final ResponseMessageInterface message,
                                                         final long peerId);
    public abstract MessageEntity processReceivedUpdateMessage(final ResponseUpdateItemInterface update,
                                                               final long peerId);
}
