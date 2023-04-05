package com.mcdead.busycoder.socialcipher.updatechecker;


import android.content.Context;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class UpdateCheckerFactory {
    public static UpdateCheckerBase generateUpdateChecker(Context context)
    {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new UpdateCheckerVK(settingsNetwork.getToken(), context);
        }

        return null;
    }
}
