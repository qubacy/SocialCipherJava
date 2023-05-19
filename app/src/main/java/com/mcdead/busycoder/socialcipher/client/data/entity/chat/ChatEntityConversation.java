package com.mcdead.busycoder.socialcipher.client.data.entity.chat;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class ChatEntityConversation extends ChatEntity {
    private String m_title = null;
    private volatile List<UserEntity> m_userList = null;

    protected ChatEntityConversation(final long peerId,
                                     final String title)
    {
        super(peerId, ChatType.CONVERSATION);

        m_title = title;
        m_userList = new ArrayList<>();
    }

    protected ChatEntityConversation(final long peerId,
                                     final String title,
                                     final List<UserEntity> userList)
    {
        super(peerId, ChatType.CONVERSATION);

        m_title = title;
        m_userList = userList;
    }


    public String getTitle() {
        return m_title;
    }

    public boolean setTitle(final String title) {
        if (title == null) return false;
        if (title.isEmpty()) return false;

        m_title = title;

        return true;
    }

    public List<UserEntity> getUsersList() {
        synchronized (m_userList) {
            return new ArrayList<>(m_userList);
        }
    }

    public boolean setUsersList(final List<UserEntity> userList) {
        if (userList == null) return false;

        synchronized (m_userList) {
            if (!m_userList.isEmpty()) return false;

            m_userList = userList;
        }

        return true;
    }
}
