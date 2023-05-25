package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.message.send.ResponseSendMessageWrapper;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResult;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResultVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class MessageSenderVK extends MessageSenderBase {
    final protected VKAPIChat m_vkAPIChat;

    protected MessageSenderVK(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final VKAPIChat vkAPIChat)
    {
        super(
                token,
                peerId,
                text,
                uploadingAttachmentList,
                attachmentUploader,
                callback);

        m_vkAPIChat = vkAPIChat;
    }

    public static MessageSenderVK getInstance(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final VKAPIChat vkAPIChat)
    {
        if (token == null || peerId == 0 || text == null ||
            (uploadingAttachmentList != null && attachmentUploader == null) || vkAPIChat == null)
        {
            return null;
        }

        return new MessageSenderVK(
                token, peerId, text, uploadingAttachmentList,
                attachmentUploader, callback, vkAPIChat);
    }

    @Override
    protected Error doInBackground(Void... voids) {
        try {
            ObjectWrapper<AttachmentUploadedResult> resultAttachmentListStringWrapper =
                    new ObjectWrapper<>();

            if (m_uploadingAttachmentList != null) {
                Error uploadAttachmentsError =
                        m_attachmentUploader.uploadAttachments(
                                m_uploadingAttachmentList,
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

            Response<ResponseSendMessageWrapper> response
                    = m_vkAPIChat.sendMessage(
                            m_token,
                            m_peerId,
                            m_text,
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
    protected void onPostExecute(Error error) {
        if (m_callback == null) return;

        if (error == null)
            m_callback.onMessageSent();
        else
            m_callback.onMessageSendingError(error);
    }
}
