package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.ChatListFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoaderBase;

public class ChatListViewModel extends ViewModel {
    private ChatListFragmentCallback m_callback = null;
    private ChatListLoaderBase m_chatListLoader = null;
    private Long m_currentChatId = null;

    private boolean m_isChatListLoadingStarted = false;

    public ChatListViewModel() {
        super();
    }

    public boolean setCurrentChatId(final Long chatId) {
        if (chatId == null) return false;

        m_currentChatId = chatId;

        return true;
    }

    public boolean setCallback(final ChatListFragmentCallback callback) {
        if (callback == null || m_callback != null)
            return false;

        m_callback = callback;

        return true;
    }

    public boolean setChatListLoader(final ChatListLoaderBase chatListLoaderBase) {
        if (chatListLoaderBase == null || m_chatListLoader != null)
            return false;

        m_chatListLoader = chatListLoaderBase;

        return true;
    }

    public boolean setChatListLoadingStarted() {
        return m_isChatListLoadingStarted = true;
    }

    public Long getCurrentChatId() {
        return m_currentChatId;
    }

    public ChatListFragmentCallback getCallback() {
        return m_callback;
    }

    public ChatListLoaderBase getChatLoader() {
        return m_chatListLoader;
    }

    public boolean isChatListLoadingStarted() {
        return m_isChatListLoadingStarted;
    }

    public boolean isInitialized() {
        return (m_callback != null && m_chatListLoader != null);
    }
}
