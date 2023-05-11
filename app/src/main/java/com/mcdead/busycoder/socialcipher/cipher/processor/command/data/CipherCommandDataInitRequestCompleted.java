package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.security.PublicKey;
import java.util.HashMap;

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
}
