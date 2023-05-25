package com.mcdead.busycoder.socialcipher.client.processor.update.checker;


import android.content.Context;

import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class UpdateCheckerAsyncFactory {
    public static UpdateCheckerAsyncBase generateUpdateChecker(Context context) {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (UpdateCheckerAsyncBase)
                    generateUpdateProcessorVK(settingsNetwork.getToken(), context);
        }

        return null;
    }

    public static UpdateCheckerAsyncVK generateUpdateProcessorVK(
            final String token,
            final Context context)
    {
        if (!checkCommonArgsValidityForImpl(token, context))
            return null;

        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null) return null;

        VKAPIAttachment vkAPIAttachment = vkAPIProvider.generateAttachmentAPI();

        return new UpdateCheckerAsyncVK(token, context, vkAPIAttachment);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final Context context)
    {
        if (token == null || context == null) return false;
        if (token.isEmpty()) return false;

        return true;
    }
}
