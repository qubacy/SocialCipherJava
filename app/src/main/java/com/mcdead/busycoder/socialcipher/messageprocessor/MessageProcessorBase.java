package com.mcdead.busycoder.socialcipher.messageprocessor;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.error.Error;
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

    public abstract Error processReceivedMessage(final ResponseMessageInterface message,
                                                         final long peerId,
                                                         ObjectWrapper<MessageEntity> resultMessage);
    public abstract Error processReceivedUpdateMessage(final ResponseUpdateItemInterface update,
                                                               final long peerId,
                                                               ObjectWrapper<MessageEntity> resultMessage);
    public abstract Error processMessageAttachments(final MessageEntity message,
                                                    final long charId);
}
