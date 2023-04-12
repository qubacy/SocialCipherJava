package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.error.Error;

public abstract class MessageSenderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected long m_peerId = 0;
    protected String m_text = null;
    protected MessageSendingCallback m_callback = null;

    public MessageSenderBase(
            final String token,
            final long peerId,
            final String text,
            final MessageSendingCallback callback)
    {
        m_token = token;
        m_peerId = peerId;
        m_text = text;
        m_callback = callback;
    }
}
