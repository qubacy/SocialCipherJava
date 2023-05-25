package com.mcdead.busycoder.socialcipher.client.processor.user.loader;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIProfile;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class UserLoaderSyncFactory {
    public static UserLoaderSyncBase generateUserLoader() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (UserLoaderSyncBase) generateUserLoaderVK(
                    settingsNetwork.getToken(), (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static UserLoaderSyncVK generateUserLoaderVK(
            final String token,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(token, vkAPIProvider))
            return null;

        VKAPIProfile vkAPIProfile = vkAPIProvider.generateProfileAPI();

        if (vkAPIProfile == null)
            return null;

        return new UserLoaderSyncVK(token, vkAPIProfile);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final APIProvider apiProvider)
    {
        if (token == null || apiProvider == null) return false;
        if (token.isEmpty()) return false;

        return true;
    }
}
