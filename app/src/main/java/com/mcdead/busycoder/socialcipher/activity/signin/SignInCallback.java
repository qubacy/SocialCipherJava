package com.mcdead.busycoder.socialcipher.activity.signin;

import com.mcdead.busycoder.socialcipher.activity.signin.data.SignInData;

public interface SignInCallback {
    public void processError(final Error error);
    public void processData(final SignInData data);
}
