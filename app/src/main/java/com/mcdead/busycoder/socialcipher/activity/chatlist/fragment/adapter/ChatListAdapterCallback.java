package com.mcdead.busycoder.socialcipher.activity.chatlist.fragment.adapter;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface ChatListAdapterCallback {
    public void onRecyclerViewAdapterErrorOccurred(final Error error);
}
