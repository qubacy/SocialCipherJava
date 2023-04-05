package com.mcdead.busycoder.socialcipher.data.entity;

import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;

public class DialogTitleExtractor {
    public static String getTitleByDialog(final DialogEntity dialog) {
        if (dialog == null) return null;

        switch (dialog.getType()) {
            case CONVERSATION: return ((DialogEntityConversation) dialog).getTitle();
            case GROUP:
            case USER: return getNameByPeerId(dialog.getDialogId());
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
