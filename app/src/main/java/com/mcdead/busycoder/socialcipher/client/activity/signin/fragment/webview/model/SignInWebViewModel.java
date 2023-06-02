package com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.client.SignInWebViewClient;

public class SignInWebViewModel extends ViewModel {
    private SignInWebViewClient m_signInWebViewClient = null;

    public SignInWebViewModel() {
        super();
    }

    public boolean setSignInWebViewClient(
            final SignInWebViewClient signInWebViewClient)
    {
        if (signInWebViewClient == null || m_signInWebViewClient != null)
            return false;

        m_signInWebViewClient = signInWebViewClient;

        return true;
    }

    public SignInWebViewClient getSignInWebViewClient() {
        return m_signInWebViewClient;
    }

    public boolean isInitialized() {
        return (m_signInWebViewClient != null);
    }
}
