package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import java.util.ArrayList;
import java.util.List;

public class CipherSessionPreInitData {
    final private List<Long> m_userPeerIdList;
    final private long m_startTimeMillisecond;

    public CipherSessionPreInitData(
            final long startTimeMillisecond)
    {
        m_userPeerIdList = new ArrayList<>();
        m_startTimeMillisecond = startTimeMillisecond;
    }

    public boolean addUser(
            final long userPeerId)
    {
        if (userPeerId == 0) return false;

        for (final Long curUserPeerId : m_userPeerIdList) {
            if (curUserPeerId == userPeerId)
                return false;
        }

        m_userPeerIdList.add(userPeerId);

        return true;
    }

    public List<Long> getUserPeerIdList() {
        return m_userPeerIdList;
    }

    public long getStartTime() {
        return m_startTimeMillisecond;
    }
}
