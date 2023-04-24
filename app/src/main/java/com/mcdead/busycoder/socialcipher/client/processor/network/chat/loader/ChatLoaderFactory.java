package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class ChatLoaderFactory {
    public static ChatLoaderBase generateDialogLoader(
            final ChatLoadingCallback callback,
            final long chatId)
    {
        if (callback == null || chatId == 0) return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new ChatLoaderVK(settingsNetwork.getToken(), callback, chatId);
        }

        return null;
    }
}
