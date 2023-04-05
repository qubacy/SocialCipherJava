package com.mcdead.busycoder.socialcipher.setting;

import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

public abstract class SettingsBase {
    public abstract boolean load();
    public abstract boolean store();
    public abstract boolean isFullyInitialized();
    public abstract void setDefaults();

    public String getFilePath() {
        if (SettingsSystem.getInstance() == null)
            return null;

        return SettingsSystem.getInstance().getSettingsDir() + '/';
    }
}
