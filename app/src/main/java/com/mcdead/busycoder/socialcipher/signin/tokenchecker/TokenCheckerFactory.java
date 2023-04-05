package com.mcdead.busycoder.socialcipher.signin.tokenchecker;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class TokenCheckerFactory {
    public static TokenCheckerBase generateTokenChecker(TokenCheckResultInterface callback) {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new TokenCheckerVK(settingsNetwork.getToken(), callback);
        }

        return null;
    }
}
