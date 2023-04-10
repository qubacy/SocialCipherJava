package com.mcdead.busycoder.socialcipher.dialog;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface MessageListAdapterCallback extends MessageListItemCallback {
    public void onErrorOccurred(final Error error);
}
