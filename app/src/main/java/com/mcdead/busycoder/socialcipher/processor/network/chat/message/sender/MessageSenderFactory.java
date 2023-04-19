package com.mcdead.busycoder.socialcipher.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
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
        if (peerId == 0 || text == null || callback == null || attachmentUploader == null)
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
                    attachmentUploader,
                    callback);
        }

        return null;
    }
}
