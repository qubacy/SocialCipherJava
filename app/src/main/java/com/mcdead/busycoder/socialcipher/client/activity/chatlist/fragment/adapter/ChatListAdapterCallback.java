package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;

public interface ChatListAdapterCallback {
    public ChatEntity getChatByIndex(final int index);
    public int getChatListSize();
    public void onRecyclerViewAdapterErrorOccurred(final Error error);
}
