package com.mcdead.busycoder.socialcipher.processor.chat.attachment.uploader;

import android.content.ContentResolver;

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

        switch (settingsNetwork.getAPIType()) {
            case VK: return new AttachmentUploaderSyncVK(
                    settingsNetwork.getToken(),
                    peerId,
                    contentResolver);
        }

        return null;
    }
}
