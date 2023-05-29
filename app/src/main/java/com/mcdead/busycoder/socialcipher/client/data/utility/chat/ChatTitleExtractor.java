package com.mcdead.busycoder.socialcipher.client.data.utility.chat;

import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;

public class ChatTitleExtractor {
    public static String getTitleByChat(final ChatEntity chat) {
        if (chat == null) return null;

        switch (chat.getType()) {
            case CONVERSATION: return ((ChatEntityConversation) chat).getTitle();
            case WITH_GROUP:
            case DIALOG: return getNameByPeerId(chat.getDialogId());
        }

        return null;
    }

    private static String getNameByPeerId(final long peerId) {
        UsersStore in = UsersStore.getInstance();
        UserEntity user = in.getUserByPeerId(peerId);

        if (user == null) {
            UserEntity localUser = in.getLocalUser();

            if (localUser.getPeerId() == peerId)
                return localUser.getName();

            return null;
        }

        return user.getName();
    }
}
