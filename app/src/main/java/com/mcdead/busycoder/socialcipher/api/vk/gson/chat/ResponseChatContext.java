package com.mcdead.busycoder.socialcipher.api.vk.gson.chat;

public class ResponseChatContext {
    public static long C_CHAT_PEER_ID_OFFSET = 2000000000;

    public static int getLocalChatIdByPeerId(
            final long chatId)
    {
        if (chatId < C_CHAT_PEER_ID_OFFSET) return 0;

        return (int) (chatId - C_CHAT_PEER_ID_OFFSET);
    }
}
