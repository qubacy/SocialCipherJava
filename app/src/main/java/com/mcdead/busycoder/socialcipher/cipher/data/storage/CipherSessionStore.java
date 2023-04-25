package com.mcdead.busycoder.socialcipher.cipher.data.storage;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSession;

import java.util.HashMap;

public class CipherSessionStore {
    private static CipherSessionStore s_instance = null;

    final private HashMap<Long, CipherSession> m_cipherSideIdSessionHashMap;

    private CipherSessionStore() {
        m_cipherSideIdSessionHashMap = new HashMap<>();
    }

    public synchronized static CipherSessionStore getInstance() {
        if (s_instance == null)
            return s_instance = new CipherSessionStore();

        return s_instance;
    }

    public boolean addSession(
            final long chatId,
            final CipherSession session)
    {
        if (session == null || chatId == 0)
            return false;

        synchronized (m_cipherSideIdSessionHashMap) {
            m_cipherSideIdSessionHashMap.put(
                    chatId,
                    session);
        }

        return true;
    }

    public CipherSession getSessionByChatId(
            final long chatId)
    {
        if (chatId == 0) return null;

        synchronized (m_cipherSideIdSessionHashMap) {
            if (!m_cipherSideIdSessionHashMap.containsKey(chatId))
                return null;

            return m_cipherSideIdSessionHashMap.get(chatId);
        }
    }

    public boolean removeSessionByChatId(
            final long chatId)
    {
        if (chatId == 0) return false;

        synchronized (m_cipherSideIdSessionHashMap) {
            if (!m_cipherSideIdSessionHashMap.containsKey(chatId))
                return false;

            m_cipherSideIdSessionHashMap.remove(chatId);
        }

        return true;
    }
}
