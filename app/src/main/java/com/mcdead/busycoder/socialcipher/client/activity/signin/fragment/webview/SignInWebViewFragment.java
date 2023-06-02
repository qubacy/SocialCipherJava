package com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInCallback;
import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInContext;
import com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.client.SignInWebViewClient;
import com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.client.SignInWebViewClientGenerator;
import com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.model.SignInWebViewModel;

public class SignInWebViewFragment extends Fragment
{
    private SignInWebViewModel m_signInWebViewModel = null;

    private SignInWebViewClient m_signInWebViewClient = null;
    private WebView m_webView = null;

    public SignInWebViewFragment() {
        super();
    }

    protected SignInWebViewFragment(final SignInWebViewClient signInWebViewClient) {
        super();

        m_signInWebViewClient = signInWebViewClient;
    }

    public static SignInWebViewFragment getInstance() {
        SignInWebViewClient signInWebViewClient =
                SignInWebViewClientGenerator.generateSignInWebViewClient(null);

        if (signInWebViewClient == null) return null;

        return new SignInWebViewFragment(signInWebViewClient);
    }

    public static SignInWebViewFragment getInstance(
            final SignInWebViewClient signInWebViewClient)
    {
        if (signInWebViewClient == null) return null;

        return new SignInWebViewFragment(signInWebViewClient);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_signInWebViewModel = new ViewModelProvider(getActivity()).get(SignInWebViewModel.class);

        if (!m_signInWebViewModel.isInitialized()) {
            if (m_signInWebViewClient == null) {
                SignInWebViewClient signInWebViewClient =
                        SignInWebViewClientGenerator.
                                generateSignInWebViewClient((SignInCallback) getActivity());

                if (signInWebViewClient == null) {
                    ErrorBroadcastReceiver.broadcastError(
                            new Error(
                                    "Sign In Web View Client generation has been failed!",
                                    true),
                            getActivity().getApplicationContext()
                    );

                    return;
                }

            } else
                m_signInWebViewClient.setCallback((SignInCallback) getActivity());

            m_signInWebViewModel.setSignInWebViewClient(m_signInWebViewClient);

        } else {
            m_signInWebViewClient = m_signInWebViewModel.getSignInWebViewClient();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_signin_web_view, container, false);

        m_webView = view.findViewById(R.id.signin_web_view);

        m_webView.setWebViewClient(m_signInWebViewClient);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        m_webView.loadUrl(SignInContext.createSignInUrl());
    }
}
