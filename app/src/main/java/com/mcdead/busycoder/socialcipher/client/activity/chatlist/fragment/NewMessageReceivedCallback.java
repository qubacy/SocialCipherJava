package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface NewMessageReceivedCallback {
    public void onNewMessageReceived(final long chatId);
    public void onNewMessageReceivingError(final Error error);
}
