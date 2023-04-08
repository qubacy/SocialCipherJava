package com.mcdead.busycoder.socialcipher.dialog.dialogloader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.error.Error;

public abstract class DialogLoaderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected DialogLoadingCallback m_callback = null;
    protected long m_chatId = 0;

    public DialogLoaderBase(
            final String token,
            final DialogLoadingCallback callback,
            final long chatId)
    {
        m_token = token;
        m_callback = callback;
        m_chatId = chatId;
    }
}
