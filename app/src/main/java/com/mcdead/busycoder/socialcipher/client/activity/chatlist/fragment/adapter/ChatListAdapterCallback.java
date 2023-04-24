package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatListAdapterCallback {
    public void onRecyclerViewAdapterErrorOccurred(final Error error);
}
