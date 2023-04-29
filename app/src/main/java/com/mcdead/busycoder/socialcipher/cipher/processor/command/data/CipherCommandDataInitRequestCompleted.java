package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.security.PublicKey;
import java.util.HashMap;

public class CipherCommandDataInitRequestCompleted extends CipherCommandData {
    final HashMap<Long, Integer> m_peerIdSideIdHashMap;
    final PublicKey m_publicKey;
    final byte[] m_sidePublicData;

    private CipherCommandDataInitRequestCompleted(
            final HashMap<Long, Integer> peerIdSideIdHashMap,
            final PublicKey publicKey,
            final byte[] sidePublicData)
    {
        m_peerIdSideIdHashMap = peerIdSideIdHashMap;
        m_publicKey = publicKey;
        m_sidePublicData = sidePublicData;
    }

    public static CipherCommandDataInitRequestCompleted getInstance(
            final HashMap<Long, Integer> peerIdSideIdHashMap,
            final PublicKey publicKey,
            final byte[] sidePublicData)
    {
        if (peerIdSideIdHashMap == null
         || publicKey == null
         || sidePublicData == null)
        {
            return null;
        }
        if (peerIdSideIdHashMap.isEmpty() || sidePublicData.length <= 0)
            return null;

        return new CipherCommandDataInitRequestCompleted(
                peerIdSideIdHashMap,
                publicKey,
                sidePublicData);
    }

    public HashMap<Long, Integer> getPeerIdSideIdHashMap() {
        return m_peerIdSideIdHashMap;
    }

    public PublicKey getPublicKey() {
        return m_publicKey;
    }

    public byte[] getSidePublicData() {
        return m_sidePublicData;
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_COMPLETED;
    }
}
