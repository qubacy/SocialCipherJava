package com.mcdead.busycoder.socialcipher.client.data.entity.chat.side;

import com.mcdead.busycoder.socialcipher.client.api.APIType;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class ChatSideDefinerFactory {
    public static ChatSideDefiner generateChatSideDefiner() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null)
            return null;

        APIType apiType = settingsNetwork.getAPIType();

        if (apiType == null)
            return null;

        switch (apiType) {
            case VK: return generateChatSideDefinerVK();
        }

        return null;
    }

    private static ChatSideDefinerVK generateChatSideDefinerVK() {
        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null)
            return null;

        UserEntity localUser = usersStore.getLocalUser();

        if (localUser == null)
            return null;

        return new ChatSideDefinerVK(localUser.getPeerId());
    }
}
