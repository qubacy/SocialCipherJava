package com.mcdead.busycoder.socialcipher.data.entity.chat.chattype;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class ChatTypeDefinerFactory {
    public static ChatTypeDefinerInterface generateDialogTypeDefiner() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateDialogTypeDefinerVK();
        }

        return null;
    }

    private static ChatTypeDefinerVK generateDialogTypeDefinerVK() {
        return new ChatTypeDefinerVK();
    }
}
