package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import java.util.concurrent.Executor;

public class MessageSenderFactory {
    public static MessageSenderBase generateMessageSender(
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final Executor executor)
    {
        if (!checkCommonArgsValidity(attachmentUploader, callback, executor))
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
                    (AttachmentUploaderSyncVK) attachmentUploader,
                    callback,
                    executor,
                    (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static MessageSenderVK generateMessageSenderVK(
            final String token,
            final AttachmentUploaderSyncVK attachmentUploaderVK,
            final MessageSendingCallback callback,
            final Executor executor,
            final VKAPIProvider apiProvider)
    {
        if (!checkCommonArgsValidityForImpl(
                token, (AttachmentUploaderSyncBase) attachmentUploaderVK,
                callback, executor, apiProvider))
        {
            return null;
        }

        VKAPIChat vkAPIChat = apiProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return new MessageSenderVK(
                token,
                attachmentUploaderVK,
                callback,
                executor,
                vkAPIChat);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final Executor executor,
            final APIProvider apiProvider)
    {
        if (!checkCommonArgsValidity(attachmentUploader, callback, executor) ||
            apiProvider == null || token == null)
        {
            return false;
        }
        if (token.isEmpty()) return false;

        return true;
    }

    private static boolean checkCommonArgsValidity(
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback,
            final Executor executor)
    {
        if (attachmentUploader == null || executor == null)
            return false;

        return true;
    }
}
