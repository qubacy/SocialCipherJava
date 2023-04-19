package com.mcdead.busycoder.socialcipher.data.utility.chat;

import com.mcdead.busycoder.socialcipher.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;

public class ChatTitleExtractor {
    public static String getTitleByDialog(final ChatEntity dialog) {
        if (dialog == null) return null;

        switch (dialog.getType()) {
            case CONVERSATION: return ((ChatEntityConversation) dialog).getTitle();
            case WITH_GROUP:
            case DIALOG: return getNameByPeerId(dialog.getDialogId());
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
