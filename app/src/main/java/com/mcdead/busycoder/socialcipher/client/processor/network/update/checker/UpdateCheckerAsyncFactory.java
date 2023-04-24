package com.mcdead.busycoder.socialcipher.client.processor.update.checker;


import android.content.Context;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class UpdateCheckerAsyncFactory {
    public static UpdateCheckerAsyncBase generateUpdateChecker(Context context)
    {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new UpdateCheckerAsyncVK(settingsNetwork.getToken(), context);
        }

        return null;
    }
}
