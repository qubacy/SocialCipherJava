package com.mcdead.busycoder.socialcipher.data.entity;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityGroup;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityUser;

public class DialogGenerator {
    public static DialogEntity generateDialogByType(
            final DialogType dialogType,
            final long dialogId)
    {
        if (dialogType == null) return null;

        switch (dialogType) {
            case GROUP:        return new DialogEntityGroup(dialogId);
            case USER:         return new DialogEntityUser(dialogId);
            case CONVERSATION: return new DialogEntityConversation(dialogId, null);
        }

        return null;
    }
}
