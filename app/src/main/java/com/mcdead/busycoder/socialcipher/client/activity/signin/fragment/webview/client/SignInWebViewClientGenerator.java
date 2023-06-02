package com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.webview.client;

import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInCallback;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class SignInWebViewClientGenerator {
    public static SignInWebViewClient generateSignInWebViewClient(
            final SignInCallback callback)
    {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (SignInWebViewClient) SignInWebViewClientVK.getInstance(callback);
        }

        return null;
    }
}
