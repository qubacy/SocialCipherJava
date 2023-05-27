package com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefiner;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorBase;

public abstract class ChatListLoaderBase extends AsyncTask<Void, Void, Error> {
    final protected String m_token;
    final protected ChatTypeDefiner m_chatTypeDefiner;

    final protected UserLoaderSyncBase m_userLoader;
    final protected MessageProcessorBase m_messageProcessor;

    protected ChatListLoadingCallback m_callback;

    protected ChatListLoaderBase(
            final String token,
            final ChatTypeDefiner chatTypeDefiner,
            final ChatListLoadingCallback callback,
            final UserLoaderSyncBase userLoader,
            final MessageProcessorBase messageProcessor)
    {
        m_token = token;
        m_chatTypeDefiner = chatTypeDefiner;
        m_callback = callback;
        m_userLoader = userLoader;
        m_messageProcessor = messageProcessor;
    }

    public boolean setCallback(
            final ChatListLoadingCallback callback)
    {
        if (callback == null) return false;

        m_callback = callback;

        return true;
    }
}
