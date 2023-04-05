package com.mcdead.busycoder.socialcipher.signin;

public interface SignInCallback {
    public void processError(final Error error);
    public void processData(final SignInData data);
}
