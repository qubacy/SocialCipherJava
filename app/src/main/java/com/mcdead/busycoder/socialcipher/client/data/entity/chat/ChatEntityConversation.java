package com.mcdead.busycoder.socialcipher.client.data.entity.chat;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.chattype.ChatType;

import java.util.ArrayList;
import java.util.List;

public class ChatEntityConversation extends ChatEntity {
    private String m_title = null;
    private volatile List<Long> m_userIdList = null;

    protected ChatEntityConversation(final long peerId,
                                     final String title)
    {
        super(peerId, ChatType.CONVERSATION);

        m_title = title;
        m_userIdList = new ArrayList<>();
    }

    protected ChatEntityConversation(final long peerId,
                                     final String title,
                                     final List<Long> userIdList)
    {
        super(peerId, ChatType.CONVERSATION);

        m_title = title;
        m_userIdList = userIdList;
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

    public List<Long> getUsersList() {
        synchronized (m_userIdList) {
            return new ArrayList<>(m_userIdList);
        }
    }

    public boolean setUsersList(final List<Long> userIdList) {
        if (userIdList == null) return false;

        synchronized (m_userIdList) {
            if (!m_userIdList.isEmpty()) return false;

            m_userIdList = userIdList;
        }

        return true;
    }
}
