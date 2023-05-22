package com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
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
    final protected AttachmentTypeDefinerInterface m_attachmentTypeDefiner;
    final protected String m_token;
    final protected ChatIdChecker m_chatIdChecker;

    public MessageProcessorBase(
            final AttachmentTypeDefinerInterface attachmentTypeDefiner,
            final String token,
            final ChatIdChecker chatIdChecker)
    {
        m_attachmentTypeDefiner = attachmentTypeDefiner;
        m_token = token;
        m_chatIdChecker = chatIdChecker;
    }

    public abstract Error processReceivedMessage(
            final ResponseMessageInterface message,
            final long chatId,
            final UserEntity senderUser,
            ObjectWrapper<MessageEntity> resultMessage);
    public abstract Error processReceivedUpdateMessage(
            final ResponseUpdateItemInterface update,
            final long chatId,
            final UserEntity senderUser,
            ObjectWrapper<MessageEntity> resultMessage);
    public abstract Error processMessageAttachments(
            final MessageEntity message,
            final long charId);
}
