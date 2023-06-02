package com.mcdead.busycoder.socialcipher.client.processor.update.checker;

import android.content.Context;

public abstract class UpdateCheckerAsyncBase implements Runnable {
    final protected String m_token;
    final protected Context m_context;

    protected UpdateCheckerAsyncBase(
            final String token,
            final Context context)
    {
        m_token = token;
        m_context = context;
    }

    public abstract void interruptChecking();
}
