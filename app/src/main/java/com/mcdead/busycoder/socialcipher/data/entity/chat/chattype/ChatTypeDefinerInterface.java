package com.mcdead.busycoder.socialcipher.data.entity.chat.chattype;

import com.mcdead.busycoder.socialcipher.api.common.gson.chat.ResponseChatListItemInterface;

public interface ChatTypeDefinerInterface {
    public ChatType getDialogType(final ResponseChatListItemInterface dialogItem);
}
