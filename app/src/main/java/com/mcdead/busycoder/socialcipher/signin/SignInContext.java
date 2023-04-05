package com.mcdead.busycoder.socialcipher.signin;

public class SignInContext {
    private static final String C_BASIC_LOGIN_URL = "https://oauth.vk.com/oauth/authorize";

    private static final String C_CLIENT_ID_PROP_NAME = "client_id";
    private static final String C_SCOPE_PROP_NAME = "scope";
    private static final String C_RESPONSE_TYPE_PROP_NAME = "response_type";
    private static final String C_REDIRECT_URI_PROP_NAME = "redirect_uri";
    private static final String C_DISPLAY_TYPE_PROP_NAME = "display";

    private static final long C_APP_ID = 2685278;
    private static final long C_SCOPE = 1073737727L;
    private static final String C_RESPONSE_TYPE = "token";
    private static final String C_REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private static final String C_DISPLAY_TYPE = "mobile";

    public static String createSignInUrl() {
        return C_BASIC_LOGIN_URL
                + '?' + C_CLIENT_ID_PROP_NAME
                + '=' + String.valueOf(C_APP_ID)
                + '&' + C_SCOPE_PROP_NAME
                + '=' + C_SCOPE
                + '&' + C_RESPONSE_TYPE_PROP_NAME
                + '=' + C_RESPONSE_TYPE
                + '&' + C_DISPLAY_TYPE_PROP_NAME
                + '=' + C_DISPLAY_TYPE
                + '&' + C_REDIRECT_URI_PROP_NAME
                + '=' + C_REDIRECT_URI;
    }
}
