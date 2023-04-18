package com.mcdead.busycoder.socialcipher.data.entity;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityGroup;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityUser;

public class DialogGenerator {
    public static DialogEntity generateChatByType(
            final DialogType chatType,
            final long chatId)
    {
        if (chatType == null) return null;

        switch (chatType) {
            case GROUP:        return new DialogEntityGroup(chatId);
            case USER:         return new DialogEntityUser(chatId);
            case CONVERSATION: return new DialogEntityConversation(chatId, null);
        }

        return null;
    }
}
