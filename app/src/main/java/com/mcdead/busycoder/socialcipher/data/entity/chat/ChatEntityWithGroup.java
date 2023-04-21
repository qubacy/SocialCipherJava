package com.mcdead.busycoder.socialcipher.data.entity.chat;

import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatType;

public class ChatEntityWithGroup extends ChatEntity {

    protected ChatEntityWithGroup(long peerId) {
        super(peerId, ChatType.WITH_GROUP);
    }
}
