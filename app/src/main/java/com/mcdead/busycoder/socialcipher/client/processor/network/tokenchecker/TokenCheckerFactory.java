package com.mcdead.busycoder.socialcipher.client.processor.tokenchecker;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
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

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (TokenCheckerBase) generateTokenCheckerVK(
                    settingsNetwork.getToken(), callback, (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static TokenCheckerVK generateTokenCheckerVK(
            final String token,
            final TokenCheckResultInterface callback,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(token, callback, vkAPIProvider))
            return null;

        VKAPIProfile vkAPIProfile = vkAPIProvider.generateProfileAPI();

        if (vkAPIProfile == null)
            return null;

        return new TokenCheckerVK(
                token, callback, vkAPIProfile);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final TokenCheckResultInterface callback,
            final APIProvider apiProvider)
    {
        if (token == null || apiProvider == null)
            return false;
        if (token.isEmpty()) return false;

        return true;
    }
}
