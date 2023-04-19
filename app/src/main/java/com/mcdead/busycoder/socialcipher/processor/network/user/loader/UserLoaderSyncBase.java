package com.mcdead.busycoder.socialcipher.processor.user.loader;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public abstract class UserLoaderSyncBase {
    protected String m_token = null;

    public UserLoaderSyncBase(
            final String token)
    {
        m_token = token;
    }

    public abstract Error loadUserById(final long userId);
    public abstract Error loadGroupById(final long groupId);
}
