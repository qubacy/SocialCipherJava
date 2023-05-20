package com.mcdead.busycoder.socialcipher.client.processor.tokenchecker;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIProfile;
import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResultInterface;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class TokenCheckerFactory {
    public static TokenCheckerBase generateTokenChecker(
            final TokenCheckResultInterface callback)
    {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateTokenCheckerVK(settingsNetwork.getToken(), callback);
        }

        return null;
    }

    public static TokenCheckerBase generateTokenCheckerVK(
            final String token,
            final TokenCheckResultInterface callback)
    {
        VKAPIProvider vkAPIProvider = new VKAPIProvider();
        VKAPIProfile vkAPIProfile = vkAPIProvider.generateProfileAPI();

        if (vkAPIProfile == null)
            return null;

        return (TokenCheckerBase)(new TokenCheckerVK(
                token, callback, vkAPIProfile));
    }
}
