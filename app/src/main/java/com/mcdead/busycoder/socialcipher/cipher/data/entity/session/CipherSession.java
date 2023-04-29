package com.mcdead.busycoder.socialcipher.cipher.data.entity.session;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInit;

import java.util.HashMap;
import java.util.Map;

public class CipherSession {
    private CipherSessionState m_state;

    final private int m_localSessionSideId;
    final private HashMap<Long, Integer> m_userPeerIdSessionSideIdHashMap;

    protected CipherSession(
            final CipherSessionStateInit initState,
            final int localSessionSideId,
            final HashMap<Long, Integer> userPeerIdSessionSideIdHashMap)
    {
        m_state = initState;

        m_localSessionSideId = localSessionSideId;
        m_userPeerIdSessionSideIdHashMap = userPeerIdSessionSideIdHashMap;
    }

    public CipherSessionState getState() {
        return m_state;
    }

    public boolean setSessionState(
            final CipherSessionState newState)
    {
        if (newState == null) return false;
        if (newState.getOverallState() == m_state.getOverallState())
            return false;

        m_state = newState;

        return true;
    }

    public int getLocalSessionSideId() {
        return m_localSessionSideId;
    }

    public int getSessionSideCount() {
        return m_userPeerIdSessionSideIdHashMap.size();
    }

//    public Long getUserPeerIdWithSideId(final int sideId) {
//        if (sideId < 0) return null;
//        if (m_sessionSideIdUserPeerIdHashMap.containsKey(sideId))
//            return null;
//
//        return m_sessionSideIdUserPeerIdHashMap.get(sideId);
//    }
//
//    public Integer getSideIdWithUserPeerId(final long userPeerId) {
//        if (userPeerId == 0) return null;
//
//        for (final Map.Entry<Integer, Long> sideIdUserPeerIdEntry :
//                m_sessionSideIdUserPeerIdHashMap.entrySet())
//        {
//            if (sideIdUserPeerIdEntry.getValue() == userPeerId)
//                return sideIdUserPeerIdEntry.getKey();
//        }
//
//        return null;
//    }

    public HashMap<Long, Integer> getUserPeerIdSessionSideIdHashMap() {
        return m_userPeerIdSessionSideIdHashMap;
    }
}
