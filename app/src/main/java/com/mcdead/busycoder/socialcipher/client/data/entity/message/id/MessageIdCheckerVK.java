package com.mcdead.busycoder.socialcipher.client.data.entity.message.id;

public class MessageIdCheckerVK implements MessageIdChecker {
    @Override
    public boolean isValid(final long messageId) {
        if (messageId == 0) return false;

        return true;
    }
}
