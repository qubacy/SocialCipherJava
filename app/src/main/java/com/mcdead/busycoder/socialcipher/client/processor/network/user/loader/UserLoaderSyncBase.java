package com.mcdead.busycoder.socialcipher.client.processor.user.loader;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

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
