package com.mcdead.busycoder.socialcipher.userloadersync;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class UserLoaderSyncFactory {
    public static UserLoaderSyncBase generateUserLoader() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new UserLoaderSyncVK(settingsNetwork.getToken());
        }

        return null;
    }
}
