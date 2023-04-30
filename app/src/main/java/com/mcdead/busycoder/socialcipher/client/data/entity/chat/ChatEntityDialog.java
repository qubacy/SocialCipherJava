package com.mcdead.busycoder.socialcipher.client.data.entity.chat;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;

public class ChatEntityDialog extends ChatEntity {

    protected ChatEntityDialog(long peerId) {
        super(peerId, ChatType.DIALOG);
    }
}
