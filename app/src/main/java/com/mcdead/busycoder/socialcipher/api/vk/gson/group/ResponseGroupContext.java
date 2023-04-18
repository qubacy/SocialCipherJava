package com.mcdead.busycoder.socialcipher.api.vk.gson.group;

public class ResponseGroupContext {
    public static boolean isChatGroupId(final long chatId) {
        if (chatId >= 0)
            return false;

        return true;
    }
}
