package com.mcdead.busycoder.socialcipher.data.dialogtype;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialogs.ResponseDialogsItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsItem;

public class DialogTypeDefinerVK implements DialogTypeDefinerInterface {
    public static final String C_USER_TYPE_NAME = "user";
    public static final String C_CHAT_TYPE_NAME = "chat";
    public static final String C_GROUP_TYPE_NAME = "group";

    public static final long C_CHAT_ID_SHIFT = 2000000000;

    @Override
    public DialogType getDialogType(final ResponseDialogsItemInterface dialogItem) {
        if (dialogItem == null) return null;

        ResponseDialogsItem dialogsItemVK = (ResponseDialogsItem) dialogItem;

        switch (dialogsItemVK.conversation.peer.type) {
            case C_USER_TYPE_NAME: return DialogType.USER;
            case C_CHAT_TYPE_NAME: return DialogType.CONVERSATION;
            case C_GROUP_TYPE_NAME: return DialogType.GROUP;
        }

        return null;
    }

    public DialogType getDialogTypeByPeerId(final long peerId) {
        if (peerId < 0)
            return DialogType.GROUP;
        if (peerId > C_CHAT_ID_SHIFT)
            return DialogType.CONVERSATION;
        if (peerId > 0 && peerId < C_CHAT_ID_SHIFT)
            return DialogType.USER;

        return null;
    }
}
