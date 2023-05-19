package com.mcdead.busycoder.socialcipher.client.data.entity.chat.id;

public class ChatIdCheckerVK implements ChatIdChecker {
    @Override
    public boolean isValid(final long chatId) {
        if (chatId == 0) return false;

        return true;
    }
}
