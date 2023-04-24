package com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result;

import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public class TokenCheckResult {
    private UserEntity m_localUser = null;
    private Error m_error = null;
    private boolean m_isSucceeded = false;

    public TokenCheckResult(
            final UserEntity localUser,
            final Error message,
            final boolean isSucceeded)
    {
        m_localUser = localUser;
        m_error = message;
        m_isSucceeded = isSucceeded;
    }

    public Error getError() {
        return m_error;
    }

    public boolean isSucceeded() {
        return m_isSucceeded;
    }

    public UserEntity getLocalUser() {
        return m_localUser;
    }
}
