package com.mcdead.busycoder.socialcipher.signin.tokenchecker;

import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.error.Error;

public interface TokenCheckResultInterface {
    public void onTokenCheckSuccess(final UserEntity localUser);
    public void onTokenCheckFailure(final Error error);
}
