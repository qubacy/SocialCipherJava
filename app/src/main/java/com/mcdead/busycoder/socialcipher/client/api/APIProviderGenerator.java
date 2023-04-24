package com.mcdead.busycoder.socialcipher.client.api;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class APIProviderGenerator {
    public static APIProvider generateAPIProvider() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new VKAPIProvider();
        }

        return null;
    }
}
