package com.mcdead.busycoder.socialcipher.setting.system;

public class SettingsSystem {
    private static SettingsSystem s_instance = null;

    private String m_settingsDir = null;
    private String m_attachmentsDir = null;

    private SettingsSystem() {

    }

    public static boolean init(final String settingsDir,
                               final String attachmentsDir)
    {
        if (s_instance == null)
            s_instance = new SettingsSystem();

        if (s_instance.m_settingsDir != null || settingsDir == null
         || s_instance.m_attachmentsDir != null || attachmentsDir == null)
        {
            return false;
        }
        if (settingsDir.isEmpty() || attachmentsDir.isEmpty())
            return false;

        s_instance.m_settingsDir = settingsDir;
        s_instance.m_attachmentsDir = attachmentsDir;

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
}
