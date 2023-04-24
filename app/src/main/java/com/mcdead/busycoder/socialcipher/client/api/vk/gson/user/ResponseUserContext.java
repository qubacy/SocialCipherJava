package com.mcdead.busycoder.socialcipher.client.api.vk.gson.user;

import static com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.ResponseChatContext.C_CHAT_PEER_ID_OFFSET;

public class ResponseUserContext {
    public static boolean isUserId(final long userId) {
        if (userId <= 0 || userId >= C_CHAT_PEER_ID_OFFSET)
            return false;

        return true;
    }
}
