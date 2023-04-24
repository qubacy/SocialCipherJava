package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

import androidx.lifecycle.ViewModel;

public class ChatListViewModel extends ViewModel {
    private long m_currentChatId = 0;

    public ChatListViewModel() {
        super();
    }

    public void setCurrentChatId(final long chatId) {
        m_currentChatId = chatId;
    }

    public long getCurrentChatId() {
        return m_currentChatId;
    }
}
