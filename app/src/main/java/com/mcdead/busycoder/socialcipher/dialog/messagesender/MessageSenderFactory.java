package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import android.content.ContentResolver;
import android.net.Uri;

import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import java.util.List;

public class MessageSenderFactory {
    public static MessageSenderBase generateMessageSender(
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final MessageSendingCallback callback,
            final ContentResolver contentResolver)
    {
        if (peerId == 0 || text == null || callback == null || contentResolver == null)
            return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new MessageSenderVK(
                    settingsNetwork.getToken(),
                    peerId,
                    text,
                    uploadingAttachmentList,
                    callback,
                    contentResolver);
        }

        return null;
    }
}
