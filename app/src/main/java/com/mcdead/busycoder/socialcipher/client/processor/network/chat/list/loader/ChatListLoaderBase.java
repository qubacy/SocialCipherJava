package com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncBase;

public abstract class ChatListLoaderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected ChatTypeDefinerVK m_dialogTypeDefiner = null;
    protected ChatListLoadingCallback m_callback = null;

    protected UserLoaderSyncBase m_userLoader = null;

    public ChatListLoaderBase(
            final String token,
            final ChatTypeDefinerVK dialogTypeDefiner,
            final ChatListLoadingCallback callback,
            final UserLoaderSyncBase userLoader)
    {
        m_token = token;
        m_dialogTypeDefiner = dialogTypeDefiner;
        m_callback = callback;
        m_userLoader = userLoader;
    }
}
