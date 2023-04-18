package com.mcdead.busycoder.socialcipher.api.vk.gson.user;

import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;

public class ResponseUserContext {
    public static boolean isUserId(final long userId) {
        if (userId <= 0 || userId >= VKAPIContext.C_CHAT_PEER_ID_OFFSET)
            return false;

        return true;
    }
}
