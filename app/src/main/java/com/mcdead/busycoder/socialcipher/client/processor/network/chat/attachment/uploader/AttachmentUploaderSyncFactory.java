package com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader;

import android.content.ContentResolver;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIUploadAttachment;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class AttachmentUploaderSyncFactory {
    public static AttachmentUploaderSyncBase generateAttachmentUploader(
            final long peerId,
            final ContentResolver contentResolver)
    {
        if (peerId == 0 || contentResolver == null)
            return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateAttachmentUploaderVK(
                    settingsNetwork.getToken(),
                    peerId,
                    contentResolver,
                    apiProvider);
        }

        return null;
    }

    public static AttachmentUploaderSyncBase generateAttachmentUploaderVK(
            final String token,
            final long peerId,
            final ContentResolver contentResolver,
            final APIProvider apiProvider)
    {
        if (!(apiProvider instanceof VKAPIProvider))
            return null;

        VKAPIUploadAttachment vkAPIUploadAttachment =
                ((VKAPIProvider) apiProvider).generateUploadAttachmentAPI();

        if (vkAPIUploadAttachment == null)
            return null;

        return (AttachmentUploaderSyncBase)(new AttachmentUploaderSyncVK(
                token, peerId, contentResolver, vkAPIUploadAttachment));
    }
}
