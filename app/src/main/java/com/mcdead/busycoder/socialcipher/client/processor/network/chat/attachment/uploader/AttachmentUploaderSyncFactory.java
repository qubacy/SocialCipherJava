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
            final ContentResolver contentResolver)
    {
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
                        contentResolver,
                        (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static AttachmentUploaderSyncVK generateAttachmentUploaderVK(
            final String token,
            final ContentResolver contentResolver,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(token, vkAPIProvider))
            return null;

        VKAPIUploadAttachment vkAPIUploadAttachment =
                vkAPIProvider.generateUploadAttachmentAPI();

        if (vkAPIUploadAttachment == null)
            return null;

        return new AttachmentUploaderSyncVK(
                token, contentResolver, vkAPIUploadAttachment);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final APIProvider apiProvider)
    {
        if (token == null || apiProvider == null)
            return false;
        if (token.isEmpty()) return false;

        return true;
    }
}
