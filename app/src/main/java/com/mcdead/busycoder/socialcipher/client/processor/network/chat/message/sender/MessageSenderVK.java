package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import android.os.Process;

import androidx.core.content.ContextCompat;

import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.message.send.ResponseSendMessageWrapper;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResult;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResultVK;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.sender.data.MessageToSendData;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Response;

public class MessageSenderVK extends MessageSenderBase {
    final protected VKAPIChat m_vkAPIChat;

    protected MessageSenderVK(
            final String token,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final Executor executor,
            final VKAPIChat vkAPIChat)
    {
        super(token, attachmentUploader, callback, executor);

        m_vkAPIChat = vkAPIChat;
    }

    public static MessageSenderVK getInstance(
            final String token,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final Executor executor,
            final VKAPIChat vkAPIChat)
    {
        if (token == null || vkAPIChat == null || attachmentUploader == null)
            return null;

        return new MessageSenderVK(
                token, attachmentUploader, callback, executor, vkAPIChat);
    }

    private Error sendMessage() {
        try {
            ObjectWrapper<AttachmentUploadedResult> resultAttachmentListStringWrapper =
                    new ObjectWrapper<>();

            if (m_messageToSendData.getUploadingAttachmentList() != null) {
                if (m_attachmentUploader == null)
                    return new Error(
                            "Attachment Uploader hasn't been provided!", true);

                Error uploadAttachmentsError =
                        m_attachmentUploader.uploadAttachments(
                                m_messageToSendData.getChatId(),
                                m_messageToSendData.getUploadingAttachmentList(),
                                resultAttachmentListStringWrapper);

                if (uploadAttachmentsError != null)
                    return uploadAttachmentsError;
            }

            AttachmentUploadedResultVK attachmentUploadedResult =
                    (AttachmentUploadedResultVK) resultAttachmentListStringWrapper.getValue();
            String attachmentListString =
                    (attachmentUploadedResult == null
                            ? ""
                            : attachmentUploadedResult.getAttachmentListString());

            Response<ResponseSendMessageWrapper> response =
                    m_vkAPIChat.sendMessage(
                            m_token,
                            m_messageToSendData.getChatId(),
                            m_messageToSendData.getText(),
                            attachmentListString).execute();

            if (!response.isSuccessful())
                return new Error("Message Sending request hasn't been accomplished!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        return null;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Error sendingError = sendMessage();

        m_executor.execute(new Runnable() {
            @Override
            public void run() {
                if (m_callback == null) return;

                if (sendingError == null)
                    m_callback.onMessageSent();
                else
                    m_callback.onMessageSendingError(sendingError);
            }
        });
    }
}
