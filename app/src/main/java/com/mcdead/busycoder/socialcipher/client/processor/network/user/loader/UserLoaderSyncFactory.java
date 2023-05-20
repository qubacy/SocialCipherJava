package com.mcdead.busycoder.socialcipher.client.processor.user.loader;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIProfile;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;

public class UserLoaderSyncFactory {
    public static UserLoaderSyncBase generateUserLoader() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateUserLoaderVK(settingsNetwork.getToken());
        }

        return null;
    }

    public static UserLoaderSyncBase generateUserLoaderVK(
            final String token)
    {
        VKAPIProvider vkAPIProvider = new VKAPIProvider();
        VKAPIProfile vkAPIProfile = vkAPIProvider.generateProfileAPI();

        if (vkAPIProfile == null)
            return null;

        return (UserLoaderSyncBase)(new UserLoaderSyncVK(
                token, vkAPIProfile));
    }
}
