package com.mcdead.busycoder.socialcipher.activity.signin.fragment;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mcdead.busycoder.socialcipher.activity.signin.SignInCallback;
import com.mcdead.busycoder.socialcipher.activity.signin.data.SignInData;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;

public class SignInWebViewClient extends WebViewClient {
    private static final char C_QUERY_DEVIDER = '#';

    private SignInCallback m_callback = null;

    public SignInWebViewClient(final SignInCallback callback) {
        m_callback = callback;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        SignInData signInResult = getSignInResult(url);

        if (signInResult == null) return;

        m_callback.processData(signInResult);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view,
                                            WebResourceRequest request)
    {
        return false;
    }

    private SignInData getSignInResult(final String url) {
        if (url == null) return null;

        String urlQuery = getUrlQuery(url);

        if (urlQuery == null) return null;

        String accessToken = getQueryPropValueByPropName(urlQuery, VKAPIContext.C_ACCESS_TOKEN_PROP_NAME);

        if (accessToken == null) return null;

        return new SignInData(accessToken);
    }

    private String getUrlQuery(final String url) {
        if (url == null) return null;

        int queryIndex = url.indexOf(C_QUERY_DEVIDER);

        if (queryIndex < 0) return null;

        return url.substring(queryIndex + 1);
    }

    private String getQueryPropValueByPropName(
            final String urlQuery,
            final String queryPropName)
    {
        if (urlQuery == null) return null;

        int accessTokenIndex = urlQuery.indexOf(queryPropName);

        if (accessTokenIndex < 0) return null;

        accessTokenIndex += (queryPropName.length() + 1);

        return getQueryValueByPropIndex(urlQuery, accessTokenIndex);
    }

    private String getQueryValueByPropIndex(
            final String query,
            final int startIndex)
    {
        if (query == null) return null;

        int queryLength = query.length();

        if (startIndex >= queryLength) return null;

        StringBuilder propValue = new StringBuilder();

        for (int i = startIndex; i < queryLength; ++i) {
            final char curChar = query.charAt(i);

            if (curChar == '&') break;

            propValue.append(curChar);
        }

        return propValue.toString();
    }
}
