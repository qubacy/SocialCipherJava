package com.mcdead.busycoder.socialcipher.signin;

public class SignInData {
    private String m_token = null;

    public SignInData(final String token) {
        m_token = token;
    }

    public String getToken() {
        return m_token;
    }

}
