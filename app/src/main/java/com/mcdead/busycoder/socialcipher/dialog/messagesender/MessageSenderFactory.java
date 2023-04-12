package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class MessageSenderFactory {
    public static MessageSenderBase generateMessageSender(
            final long peerId,
            final String text,
            final MessageSendingCallback callback)
    {
        if (peerId == 0 || text == null || callback == null)
            return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new MessageSenderVK(settingsNetwork.getToken(), peerId, text, callback);
        }

        return null;
    }
}
