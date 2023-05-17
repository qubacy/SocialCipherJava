package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Objects;

public class CipherCommandDataInitRequestCompleted extends CipherCommandData {
    final HashMap<Long, Integer> m_peerIdSideIdHashMap;
    final PublicKey m_publicKey;

    private CipherCommandDataInitRequestCompleted(
            final HashMap<Long, Integer> peerIdSideIdHashMap,
            final PublicKey publicKey)
    {
        m_peerIdSideIdHashMap = peerIdSideIdHashMap;
        m_publicKey = publicKey;
    }

    public static CipherCommandDataInitRequestCompleted getInstance(
            final HashMap<Long, Integer> peerIdSideIdHashMap,
            final PublicKey publicKey)
    {
        if (peerIdSideIdHashMap == null
         || publicKey == null)
        {
            return null;
        }
        if (peerIdSideIdHashMap.isEmpty())
            return null;

        return new CipherCommandDataInitRequestCompleted(
                peerIdSideIdHashMap,
                publicKey);
    }

    public HashMap<Long, Integer> getPeerIdSideIdHashMap() {
        return m_peerIdSideIdHashMap;
    }

    public PublicKey getPublicKey() {
        return m_publicKey;
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CipherCommandDataInitRequestCompleted that =
                (CipherCommandDataInitRequestCompleted) o;

        return Objects.equals(m_peerIdSideIdHashMap, that.m_peerIdSideIdHashMap) &&
                Objects.equals(m_publicKey, that.m_publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_peerIdSideIdHashMap, m_publicKey);
    }
}
