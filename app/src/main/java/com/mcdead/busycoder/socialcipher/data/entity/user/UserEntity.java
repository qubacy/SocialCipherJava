package com.mcdead.busycoder.socialcipher.data.entity.user;

public class UserEntity {
    private long m_peerId = 0;
    private String m_name = null;

    public UserEntity(final long peerId,
                      final String name)
    {
        m_peerId = peerId;
        m_name = name;
    }

    public long getPeerId() {
        return m_peerId;
    }

    public String getName() {
        return m_name;
    }
}
