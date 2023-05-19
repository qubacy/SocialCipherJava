package com.mcdead.busycoder.socialcipher.client.data.entity.chat.id;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class ChatIdCheckerGenerator {
    public static ChatIdChecker generateChatIdChecker() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null)
            return null;
        if (settingsNetwork.getAPIType() == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new ChatIdCheckerVK();
        }

        return null;
    }
}
