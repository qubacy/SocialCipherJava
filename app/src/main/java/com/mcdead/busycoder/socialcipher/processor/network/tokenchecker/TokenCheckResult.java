package com.mcdead.busycoder.socialcipher.processor.tokenchecker;

import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

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
