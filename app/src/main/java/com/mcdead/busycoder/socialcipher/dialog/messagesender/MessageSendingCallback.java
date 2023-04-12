package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface MessageSendingCallback {
    public void onMessageSent();
    public void onMessageSendingError(final Error error);
}
