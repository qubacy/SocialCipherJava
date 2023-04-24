package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public abstract class ChatLoaderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected ChatLoadingCallback m_callback = null;
    protected long m_chatId = 0;

    public ChatLoaderBase(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId)
    {
        m_token = token;
        m_callback = callback;
        m_chatId = chatId;
    }
}
