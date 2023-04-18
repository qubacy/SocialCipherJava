package com.mcdead.busycoder.socialcipher.api.vk.gson.chat;

import static com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext.C_CHAT_PEER_ID_OFFSET;

public class ResponseChatContext {
    public static int getLocalChatIdByPeerId(
            final long chatId)
    {
        if (chatId < C_CHAT_PEER_ID_OFFSET) return 0;

        return (int) (chatId - C_CHAT_PEER_ID_OFFSET);
    }

    public static boolean isChatConversationId(final long chatId) {
        if (chatId < C_CHAT_PEER_ID_OFFSET)
            return false;

        return true;
    }
}
