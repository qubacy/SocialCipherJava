package com.mcdead.busycoder.socialcipher.setting.system;

import android.content.Context;

import java.io.File;

public class SettingsSystem {
    private static SettingsSystem s_instance = null;

    private String m_settingsDir = null;
    private String m_attachmentsDir = null;
    private File m_cacheDir = null;

    private SettingsSystem() {

    }

    public static boolean init(
            final String settingsDir,
            final String attachmentsDir,
            final Context context)
    {
        if (s_instance == null)
            s_instance = new SettingsSystem();

        if (s_instance.m_settingsDir != null || settingsDir == null
         || s_instance.m_attachmentsDir != null || attachmentsDir == null
         || context == null)
        {
            return false;
        }
        if (settingsDir.isEmpty() || attachmentsDir.isEmpty())
            return false;

        s_instance.m_settingsDir = settingsDir;
        s_instance.m_attachmentsDir = attachmentsDir;
        s_instance.m_cacheDir = context.getCacheDir();

        return true;
    }

    public static SettingsSystem getInstance() {
        return s_instance;
    }

    public String getSettingsDir() {
        return m_settingsDir;
    }

    public String getAttachmentsDir() {
        return m_attachmentsDir;
    }

    public File getCacheDir() {
        return m_cacheDir;
    }
}
