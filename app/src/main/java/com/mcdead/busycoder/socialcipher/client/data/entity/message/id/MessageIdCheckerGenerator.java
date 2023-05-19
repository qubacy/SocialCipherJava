package com.mcdead.busycoder.socialcipher.client.data.entity.message.id;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class MessageIdCheckerGenerator {
    public static MessageIdChecker generateMessageIdChecker() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null)
            return null;
        if (settingsNetwork.getAPIType() == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new MessageIdCheckerVK();
        }

        return null;
    }
}
