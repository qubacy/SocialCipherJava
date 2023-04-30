package com.mcdead.busycoder.socialcipher.client.data.entity.chat;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;

public class ChatEntityWithGroup extends ChatEntity {

    protected ChatEntityWithGroup(long peerId) {
        super(peerId, ChatType.WITH_GROUP);
    }
}
