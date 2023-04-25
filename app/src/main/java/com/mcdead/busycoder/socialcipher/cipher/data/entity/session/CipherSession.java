package com.mcdead.busycoder.socialcipher.cipher.data.entity.session;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInit;

import java.util.HashMap;

public class CipherSession {
    private CipherSessionState m_state;

    final private int m_localSessionSideId;
    final private HashMap<Integer, Long> m_sessionSideIdUserPeerIdHashMap;

    protected CipherSession(
            final CipherSessionStateInit initState,
            final int localSessionSideId,
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        m_state = initState;

        m_localSessionSideId = localSessionSideId;
        m_sessionSideIdUserPeerIdHashMap = sessionSideIdUserPeerIdHashMap;
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

    public Long getUserPeerIdWithSideId(final int sideId) {
        if (sideId < 0) return null;
        if (m_sessionSideIdUserPeerIdHashMap.containsKey(sideId))
            return null;

        return m_sessionSideIdUserPeerIdHashMap.get(sideId);
    }
}
