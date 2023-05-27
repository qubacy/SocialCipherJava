package com.mcdead.busycoder.socialcipher.setting.manager;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.setting.cipher.SettingsCipher;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

public class SettingsManager {
    public static final String C_ATTACHMENT_DIR_NAME = "attachments";

    public static Error initializeSettings(
            final String dir,
            final Context context)
    {
        if (!SettingsSystem.init(dir, dir + '/' + C_ATTACHMENT_DIR_NAME, context))
            return new Error("System Settings initialization went wrong!", true);

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();
        SettingsCipher settingsCipher = SettingsCipher.getInstance();

        settingsNetwork.setDefaults();
        settingsCipher.setDefaults();

        if (!settingsNetwork.load()) {
            return new Error("Network Settings loading has been failed!", false);
        }
        if (!settingsCipher.load()) {
            return new Error("Ciphering Settings loading has been failed!", false);
        }

        return null;
    }

    public static boolean saveSettings() {
        return (SettingsNetwork.getInstance().store() &&
                SettingsCipher.getInstance().store());
    }
}
