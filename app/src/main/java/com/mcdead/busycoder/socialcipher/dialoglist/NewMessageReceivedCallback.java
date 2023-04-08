package com.mcdead.busycoder.socialcipher.dialoglist;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface NewMessageReceivedCallback {
    public void onNewMessageReceived(final long chatId);
    public void onNewMessageReceivingError(final Error error);
}
