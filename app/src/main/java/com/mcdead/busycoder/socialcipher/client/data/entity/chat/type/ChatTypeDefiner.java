package com.mcdead.busycoder.socialcipher.client.data.entity.chat.type;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseChatListItemInterface;

public interface ChatTypeDefiner {
    public ChatType getDialogType(final ResponseChatListItemInterface dialogItem);
}
