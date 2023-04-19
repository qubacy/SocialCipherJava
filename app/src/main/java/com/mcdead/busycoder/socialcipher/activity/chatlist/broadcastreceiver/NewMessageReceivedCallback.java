package com.mcdead.busycoder.socialcipher.activity.chatlist.broadcastreceiver;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface NewMessageReceivedCallback {
    public void onNewMessageReceived(final long chatId);
    public void onNewMessageReceivingError(final Error error);
}
