package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface MessageSendingCallback {
    public void onMessageSent();
    public void onMessageSendingError(final Error error);
}
