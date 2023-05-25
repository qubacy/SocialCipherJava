package com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader;

import android.content.ContentResolver;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIUploadAttachment;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class AttachmentUploaderSyncFactory {
    public static AttachmentUploaderSyncBase generateAttachmentUploader(
            final long chatId,
            final ContentResolver contentResolver)
    {
        if (!checkCommonArgsValidity(contentResolver))
            return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (AttachmentUploaderSyncBase)
                    generateAttachmentUploaderVK(
                        settingsNetwork.getToken(),
                        chatId,
                        contentResolver,
                        (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static AttachmentUploaderSyncVK generateAttachmentUploaderVK(
            final String token,
            final long chatId,
            final ContentResolver contentResolver,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(token, contentResolver, vkAPIProvider))
            return null;

        ChatIdCheckerVK chatIdCheckerVK = new ChatIdCheckerVK();

        if (!chatIdCheckerVK.isValid(chatId)) return null;

        VKAPIUploadAttachment vkAPIUploadAttachment =
                vkAPIProvider.generateUploadAttachmentAPI();

        if (vkAPIUploadAttachment == null)
            return null;

        return new AttachmentUploaderSyncVK(
                token, chatId, contentResolver, vkAPIUploadAttachment);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final ContentResolver contentResolver,
            final APIProvider apiProvider)
    {
        if (token == null || contentResolver == null || apiProvider == null)
            return false;
        if (token.isEmpty()) return false;

        return true;
    }

    private static boolean checkCommonArgsValidity(
            final ContentResolver contentResolver)
    {
        if (contentResolver == null) return false;

        return true;
    }
}
