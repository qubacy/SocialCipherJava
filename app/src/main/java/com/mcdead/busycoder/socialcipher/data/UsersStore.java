package com.mcdead.busycoder.socialcipher.data;

import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;

import java.util.HashMap;

public class UsersStore {
    private static UsersStore s_instance = null;

    private HashMap<Long, UserEntity> m_users = null;
    private UserEntity m_localUser = null;

    private UsersStore() {
        m_users = new HashMap<>();
    }

    public static UsersStore getInstance() {
        if (s_instance == null)
            s_instance = new UsersStore();

        return s_instance;
    }

    public UserEntity getUserByPeerId(final long peerId) {
        if (peerId == 0) return null;

        synchronized (m_users) {
            return m_users.get(peerId);
        }
    }

    public boolean addUser(final UserEntity user) {
        if (user == null) return false;

        synchronized (m_users) {
            if (m_localUser != null)
                if (m_localUser.getPeerId() == user.getPeerId())
                    return true;

            m_users.put(user.getPeerId(), user);
        }

        return true;
    }

    public boolean setLocalUser(final UserEntity localUser) {
        if (m_localUser != null || localUser == null)
            return false;

        m_localUser = localUser;

        return true;
    }

    public UserEntity getLocalUser() {
        return m_localUser;
    }

    public void clean() {
        synchronized (m_users) {
            m_users.clear();
            m_localUser = null;
        }
    }
}
