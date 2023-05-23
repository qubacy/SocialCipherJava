package com.mcdead.busycoder.socialcipher.setting.manager;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.setting.cipher.SettingsCipher;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

public class SettingsManager {
    public static final String C_ATTACHMENT_DIR_NAME = "attachments";

    public static boolean initializeSettings(
            final String dir,
            final Context context)
    {
        if (!SettingsSystem.init(dir, dir + '/' + C_ATTACHMENT_DIR_NAME, context))
            return false;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();
        SettingsCipher settingsCipher = SettingsCipher.getInstance();

        settingsNetwork.setDefaults();
        settingsCipher.setDefaults();

        return (settingsNetwork.load() && settingsCipher.load());
    }

    public static boolean saveSettings() {
        return (SettingsNetwork.getInstance().store() &&
                SettingsCipher.getInstance().store());
    }
}
