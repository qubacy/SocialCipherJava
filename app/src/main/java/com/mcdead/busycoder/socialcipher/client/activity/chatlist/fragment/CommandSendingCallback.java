package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface CommandSendingCallback {
    public void onNewCommandSendingError(final Error error);
}
