package com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.client;

import android.webkit.WebViewClient;

import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInCallback;

public class SignInWebViewClient extends WebViewClient {
    protected SignInCallback m_callback = null;

    protected SignInWebViewClient(final SignInCallback callback) {
        super();

        m_callback = callback;
    }

    public boolean setCallback(final SignInCallback callback) {
        if (m_callback != null || callback == null) return false;

        m_callback = callback;

        return true;
    }
}
