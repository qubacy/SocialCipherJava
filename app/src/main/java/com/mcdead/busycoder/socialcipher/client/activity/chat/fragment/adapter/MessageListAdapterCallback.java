package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface MessageListAdapterCallback extends MessageListItemCallback {
    public void onErrorOccurred(final Error error);
}
