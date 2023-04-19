package com.mcdead.busycoder.socialcipher.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.api.vk.webinterface.VKAPIUploadAttachment;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.message.send.ResponseSendMessageWrapper;
import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.processor.chat.attachment.uploader.result.AttachmentUploadedResult;
import com.mcdead.busycoder.socialcipher.processor.chat.attachment.uploader.result.AttachmentUploadedResultVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class MessageSenderVK extends MessageSenderBase {
    public MessageSenderVK(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback)
    {
        super(
                token,
                peerId,
                text,
                uploadingAttachmentList,
                attachmentUploader,
                callback);
    }

    @Override
    protected Error doInBackground(Void... voids) {
        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new Error("API hasn't been initialized!", true);

        VKAPIUploadAttachment vkAPIUploadAttachment =
                vkAPIProvider.generateUploadAttachmentAPI();
        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        try {
            ObjectWrapper<AttachmentUploadedResult> resultAttachmentListStringWrapper =
                    new ObjectWrapper<>();

            Error uploadAttachmentsError =
                    m_attachmentUploader.uploadAttachments(
                            vkAPIUploadAttachment,
                            m_uploadingAttachmentList,
                            resultAttachmentListStringWrapper);

            if (uploadAttachmentsError != null)
                return uploadAttachmentsError;

            AttachmentUploadedResultVK attachmentUploadedResult =
                    (AttachmentUploadedResultVK) resultAttachmentListStringWrapper.getValue();
            String attachmentListString =
                    (attachmentUploadedResult == null
                        ? ""
                        : attachmentUploadedResult.getAttachmentListString());

            Response<ResponseSendMessageWrapper> response
                    = vkAPIChat.sendMessage(
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
        if (error == null)
            m_callback.onMessageSent();
        else
            m_callback.onMessageSendingError(error);
    }
}
