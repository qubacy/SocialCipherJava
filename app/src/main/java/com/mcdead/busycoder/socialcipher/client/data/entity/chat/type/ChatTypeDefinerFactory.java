package com.mcdead.busycoder.socialcipher.client.data.entity.chat.type;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class ChatTypeDefinerFactory {
    public static ChatTypeDefiner generateDialogTypeDefiner() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateDialogTypeDefinerVK();
        }

        return null;
    }

    public static ChatTypeDefinerVK generateDialogTypeDefinerVK() {
        return new ChatTypeDefinerVK();
    }
}
