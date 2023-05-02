package com.mcdead.busycoder.socialcipher.client.activity.signin;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.signin.data.SignInData;

public interface SignInCallback {
    public void processError(final Error error);
    public void processData(final SignInData data);
}
