package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
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
        if (peerId == 0 || text == null || (uploadingAttachmentList != null && attachmentUploader == null))
            return null;

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
                    attachmentUploader,
                    callback,
                    apiProvider);
        }

        return null;
    }

    public static MessageSenderBase generateMessageSenderVK(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final APIProvider apiProvider)
    {
        if (!(apiProvider instanceof VKAPIProvider))
            return null;

        VKAPIChat vkAPIChat = ((VKAPIProvider) apiProvider).generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return (MessageSenderBase) (new MessageSenderVK(
                token,
                peerId,
                text,
                uploadingAttachmentList,
                attachmentUploader,
                callback,
                vkAPIChat
        ));
    }
}
