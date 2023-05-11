package com.mcdead.busycoder.socialcipher.cipher.data.storage;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSession;

import java.util.HashMap;

public class CipherSessionStore {
    private static CipherSessionStore s_instance = null;

    final private HashMap<Long, CipherSession> m_cipherChatIdSessionHashMap;

    private CipherSessionStore() {
        m_cipherChatIdSessionHashMap = new HashMap<>();
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

        synchronized (m_cipherChatIdSessionHashMap) {
            m_cipherChatIdSessionHashMap.put(
                    chatId,
                    session);
        }

        return true;
    }

    public CipherSession getSessionByChatId(
            final long chatId)
    {
        if (chatId == 0) return null;

        synchronized (m_cipherChatIdSessionHashMap) {
            if (!m_cipherChatIdSessionHashMap.containsKey(chatId))
                return null;

            return m_cipherChatIdSessionHashMap.get(chatId);
        }
    }

    public boolean removeSessionByChatId(
            final long chatId)
    {
        if (chatId == 0) return false;

        synchronized (m_cipherChatIdSessionHashMap) {
            if (!m_cipherChatIdSessionHashMap.containsKey(chatId))
                return false;

            m_cipherChatIdSessionHashMap.remove(chatId);
        }

        return true;
    }

    public synchronized void clean() {
        m_cipherChatIdSessionHashMap.clear();
    }
}
