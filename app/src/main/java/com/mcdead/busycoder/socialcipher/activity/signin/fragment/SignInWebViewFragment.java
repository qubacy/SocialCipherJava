package com.mcdead.busycoder.socialcipher.activity.signin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.activity.signin.SignInCallback;
import com.mcdead.busycoder.socialcipher.activity.signin.SignInContext;

public class SignInWebViewFragment extends Fragment
{
    private WebView m_webView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_signin_web_view, container, false);

        m_webView = view.findViewById(R.id.signin_web_view);

        m_webView.setWebViewClient(new SignInWebViewClient((SignInCallback) getActivity()));

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
