package com.mcdead.busycoder.socialcipher.client.data.entity.chat.type;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseChatListItemInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListItem;

public class ChatTypeDefinerVK implements ChatTypeDefinerInterface {
    public static final String C_USER_TYPE_NAME = "user";
    public static final String C_CHAT_TYPE_NAME = "chat";
    public static final String C_GROUP_TYPE_NAME = "group";

    public static final long C_CHAT_ID_SHIFT = 2000000000;

    @Override
    public ChatType getDialogType(final ResponseChatListItemInterface dialogItem) {
        if (dialogItem == null) return null;

        ResponseChatListItem dialogsItemVK = (ResponseChatListItem) dialogItem;

        switch (dialogsItemVK.conversation.peer.type) {
            case C_USER_TYPE_NAME: return ChatType.DIALOG;
            case C_CHAT_TYPE_NAME: return ChatType.CONVERSATION;
            case C_GROUP_TYPE_NAME: return ChatType.WITH_GROUP;
        }

        return null;
    }

    public ChatType getDialogTypeByPeerId(final long peerId) {
        if (peerId < 0)
            return ChatType.WITH_GROUP;
        if (peerId > C_CHAT_ID_SHIFT)
            return ChatType.CONVERSATION;
        if (peerId > 0 && peerId < C_CHAT_ID_SHIFT)
            return ChatType.DIALOG;

        return null;
    }
}
