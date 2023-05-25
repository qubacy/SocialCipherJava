package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import java.util.List;

public class MessageSenderFactory {
    public static MessageSenderBase generateMessageSender(
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback)
    {
        if (!checkCommonArgsValidity(
                peerId, text, uploadingAttachmentList, attachmentUploader, callback))
        {
            return null;
        }

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateMessageSenderVK(
                    settingsNetwork.getToken(),
                    peerId,
                    text,
                    uploadingAttachmentList,
                    (AttachmentUploaderSyncVK) attachmentUploader,
                    callback,
                    (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static MessageSenderVK generateMessageSenderVK(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncVK attachmentUploaderVK,
            final MessageSendingCallback callback,
            final VKAPIProvider apiProvider)
    {
        if (!checkCommonArgsValidityForImpl(
                token, peerId, text, uploadingAttachmentList,
                (AttachmentUploaderSyncBase) attachmentUploaderVK, callback, apiProvider))
        {
            return null;
        }

        VKAPIChat vkAPIChat = apiProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return new MessageSenderVK(
                token,
                peerId,
                text,
                uploadingAttachmentList,
                attachmentUploaderVK,
                callback,
                vkAPIChat);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final APIProvider apiProvider)
    {
        if (!checkCommonArgsValidity(
                peerId, text, uploadingAttachmentList, attachmentUploader, callback) ||
                apiProvider == null || token == null)
        {
            return false;
        }
        if (token.isEmpty()) return false;

        return true;
    }

    private static boolean checkCommonArgsValidity(
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback)
    {
        if (peerId == 0 || text == null ||
                (uploadingAttachmentList != null && attachmentUploader == null))
        {
            return false;
        }

        return true;
    }
}
