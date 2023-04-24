package com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result;

import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface TokenCheckResultInterface {
    public void onTokenCheckSuccess(final UserEntity localUser);
    public void onTokenCheckFailure(final Error error);
}
