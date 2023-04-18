package com.mcdead.busycoder.socialcipher.data.entity.dialog;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;

import java.util.ArrayList;
import java.util.List;

public class DialogEntityConversation extends DialogEntity {
    private String m_title = null;
    private volatile List<Long> m_userIdList = null;

    public DialogEntityConversation(final long peerId,
                                    final String title)
    {
        super(peerId, DialogType.CONVERSATION);

        m_title = title;
        m_userIdList = new ArrayList<>();
    }

    public DialogEntityConversation(final long peerId,
                                    final String title,
                                    final List<Long> userIdList)
    {
        super(peerId, DialogType.CONVERSATION);

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
