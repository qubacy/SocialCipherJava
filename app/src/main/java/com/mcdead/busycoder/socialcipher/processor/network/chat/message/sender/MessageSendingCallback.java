package com.mcdead.busycoder.socialcipher.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface MessageSendingCallback {
    public void onMessageSent();
    public void onMessageSendingError(final Error error);
}
