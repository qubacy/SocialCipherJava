package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

public interface ChatListFragmentCallback {
    public void onChatListLoaded();
    public void onChatItemClicked(final long chatId);
}
