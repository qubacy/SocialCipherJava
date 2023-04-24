package com.mcdead.busycoder.socialcipher.client.processor.update.checker;

import android.content.Context;

public abstract class UpdateCheckerAsyncBase implements Runnable {
    protected String m_token = null;
    protected Context m_context = null;

    public UpdateCheckerAsyncBase(final String token,
                                  Context context)
    {
        m_token = token;
        m_context = context;
    }
}
