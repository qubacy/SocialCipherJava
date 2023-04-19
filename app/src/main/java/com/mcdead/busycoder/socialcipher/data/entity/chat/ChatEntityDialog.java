package com.mcdead.busycoder.socialcipher.data.entity.chat;

import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatType;

public class ChatEntityDialog extends ChatEntity {

    public ChatEntityDialog(long peerId) {
        super(peerId, ChatType.DIALOG);
    }
}
