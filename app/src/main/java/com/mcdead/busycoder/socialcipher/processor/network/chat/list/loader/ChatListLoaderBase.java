package com.mcdead.busycoder.socialcipher.processor.chat.list.loader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public abstract class ChatListLoaderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected ChatTypeDefinerVK m_dialogTypeDefiner = null;
    protected ChatListLoadingCallback m_callback = null;

    public ChatListLoaderBase(
            final String token,
            final ChatTypeDefinerVK dialogTypeDefiner,
            final ChatListLoadingCallback callback)
    {
        m_token = token;
        m_dialogTypeDefiner = dialogTypeDefiner;
        m_callback = callback;
    }
}