package com.mcdead.busycoder.socialcipher.client.processor.tokenchecker;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResult;
import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResultInterface;

public abstract class TokenCheckerBase extends AsyncTask<Void, Void, TokenCheckResult>
{
    protected String m_token = null;
    protected TokenCheckResultInterface m_callback = null;

    protected TokenCheckerBase(
            final String token,
            final TokenCheckResultInterface callback)
    {
        m_token = token;
        m_callback = callback;
    }
}
