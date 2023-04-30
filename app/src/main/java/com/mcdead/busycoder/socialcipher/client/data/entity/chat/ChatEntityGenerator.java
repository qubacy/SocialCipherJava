package com.mcdead.busycoder.socialcipher.client.data.entity.chat;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;

public class ChatEntityGenerator {
    public static ChatEntity generateChatByType(
            final ChatType chatType,
            final long chatId)
    {
        if (chatType == null) return null;

        switch (chatType) {
            case WITH_GROUP:   return new ChatEntityWithGroup(chatId);
            case DIALOG:       return new ChatEntityDialog(chatId);
            case CONVERSATION: return new ChatEntityConversation(chatId, null);
        }

        return null;
    }
}
