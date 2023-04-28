package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.List;

public class CipherCommandDataInitRequestCompleted extends CipherCommandData {
    final List<Pair<Long, Integer>> m_peerIdSideIdPairList;
    final PublicKey m_publicKey;
    final byte[] m_sidePublicData;

    private CipherCommandDataInitRequestCompleted(
            final List<Pair<Long, Integer>> peerIdSideIdPairList,
            final PublicKey publicKey,
            final byte[] sidePublicData)
    {
        m_peerIdSideIdPairList = peerIdSideIdPairList;
        m_publicKey = publicKey;
        m_sidePublicData = sidePublicData;
    }

    public static CipherCommandDataInitRequestCompleted getInstance(
            final List<Pair<Long, Integer>> peerIdSideIdPairList,
            final PublicKey publicKey,
            final byte[] sidePublicData)
    {
        if (peerIdSideIdPairList == null
         || publicKey == null
         || sidePublicData == null)
        {
            return null;
        }
        if (peerIdSideIdPairList.isEmpty() || sidePublicData.length <= 0)
            return null;

        return new CipherCommandDataInitRequestCompleted(
                peerIdSideIdPairList,
                publicKey,
                sidePublicData);
    }

    public List<Pair<Long, Integer>> getPeerIdSideIdPairList() {
        return m_peerIdSideIdPairList;
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
