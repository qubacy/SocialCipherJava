package com.mcdead.busycoder.socialcipher.data.utility.chat;

import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatType;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityWithGroup;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityDialog;

public class ChatGenerator {
    public static ChatEntity generateChatByType(
            final ChatType chatType,
            final long chatId)
    {
        if (chatType == null) return null;

        switch (chatType) {
            case WITH_GROUP:        return new ChatEntityWithGroup(chatId);
            case DIALOG:         return new ChatEntityDialog(chatId);
            case CONVERSATION: return new ChatEntityConversation(chatId, null);
        }

        return null;
    }
}
