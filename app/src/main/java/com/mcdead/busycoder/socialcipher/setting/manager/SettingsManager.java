package com.mcdead.busycoder.socialcipher.setting.manager;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

public class SettingsManager {
    public static boolean initializeSettings(final String dir)
    {
        if (!SettingsSystem.init(dir, dir + "/attachments")) return false;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        settingsNetwork.setDefaults();

        return settingsNetwork.load();
    }

    public static boolean saveSettings() {
        return (SettingsNetwork.getInstance().store());
    }
}
