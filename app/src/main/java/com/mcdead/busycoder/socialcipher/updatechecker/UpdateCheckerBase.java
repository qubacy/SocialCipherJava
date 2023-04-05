package com.mcdead.busycoder.socialcipher.updatechecker;

import android.content.Context;

public abstract class UpdateCheckerBase implements Runnable {
    protected String m_token = null;
    protected Context m_context = null;

    public UpdateCheckerBase(final String token,
                             Context context)
    {
        m_token = token;
        m_context = context;
    }
}
