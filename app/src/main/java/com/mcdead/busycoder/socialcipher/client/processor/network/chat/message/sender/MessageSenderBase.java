package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.sender.data.MessageToSendData;

import java.util.concurrent.Executor;

public abstract class MessageSenderBase implements Runnable {
    final protected String m_token;
    final protected AttachmentUploaderSyncBase m_attachmentUploader;
    final protected Executor m_executor;

    protected MessageSendingCallback m_callback = null;
    protected MessageToSendData m_messageToSendData = null;

    protected MessageSenderBase(
            final String token,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final Executor executor)
    {
        m_token = token;

        m_attachmentUploader = attachmentUploader;
        m_callback = callback;

        m_executor = executor;
    }

    public boolean setCallback(
            final MessageSendingCallback callback)
    {
        if (callback == null || m_callback != null)
            return false;

        m_callback = callback;

        return true;
    }

    public boolean execute(
            final MessageToSendData messageToSendData)
    {
        if (messageToSendData == null) return false;

        m_messageToSendData = messageToSendData;

        new Thread(this).start();

        return true;
    }
}
