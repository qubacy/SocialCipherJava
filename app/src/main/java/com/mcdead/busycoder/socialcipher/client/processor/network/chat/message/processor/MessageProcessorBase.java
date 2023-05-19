package com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

/*
*
* MessageProcessor can be used as a dependent unit; so
* it hasn't to have any proactive facilities (broadcasting
* errors etc.);
*
*/

public abstract class MessageProcessorBase {
    protected AttachmentTypeDefinerInterface m_attachmentTypeDefiner = null;
    protected String m_token = null;

    public MessageProcessorBase(final AttachmentTypeDefinerInterface attachmentTypeDefiner,
                                final String token)
    {
        m_attachmentTypeDefiner = attachmentTypeDefiner;
        m_token = token;
    }

    public abstract Error processReceivedMessage(
            final ResponseMessageInterface message,
            final long peerId,
            final UserEntity senderUser,
            ObjectWrapper<MessageEntity> resultMessage);
    public abstract Error processReceivedUpdateMessage(
            final ResponseUpdateItemInterface update,
            final long peerId,
            final UserEntity senderUser,
            ObjectWrapper<MessageEntity> resultMessage);
    public abstract Error processMessageAttachments(
            final MessageEntity message,
            final long charId);
}
