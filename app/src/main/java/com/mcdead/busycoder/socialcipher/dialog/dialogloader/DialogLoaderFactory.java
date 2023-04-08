package com.mcdead.busycoder.socialcipher.dialog.dialogloader;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class DialogLoaderFactory {
    public static DialogLoaderBase generateDialogLoader(
            final DialogLoadingCallback callback,
            final long chatId)
    {
        if (callback == null || chatId == 0) return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new DialogLoaderVK(settingsNetwork.getToken(), callback, chatId);
        }

        return null;
    }
}
