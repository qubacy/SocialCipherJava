package com.mcdead.busycoder.socialcipher.processor.tokenchecker;

import android.os.AsyncTask;

public abstract class TokenCheckerBase extends AsyncTask<Void, Void, TokenCheckResult>
{
    protected String m_token = null;
    protected TokenCheckResultInterface m_callback = null;

    public TokenCheckerBase(final String token,
                            TokenCheckResultInterface callback)
    {
        m_token = token;
        m_callback = callback;
    }
}
