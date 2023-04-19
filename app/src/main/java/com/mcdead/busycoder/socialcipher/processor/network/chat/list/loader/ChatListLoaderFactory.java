package com.mcdead.busycoder.socialcipher.processor.chat.list.loader;

import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class ChatListLoaderFactory {
    public static ChatListLoaderBase generateDialogsLoader(
            final ChatListLoadingCallback callback)
    {
        if (callback == null) return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        ChatTypeDefinerInterface dialogTypeDefiner = ChatTypeDefinerFactory.generateDialogTypeDefiner();

        if (dialogTypeDefiner == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new ChatListLoaderVK(settingsNetwork.getToken(), (ChatTypeDefinerVK) dialogTypeDefiner, callback);
        }

        return null;
    }
}
