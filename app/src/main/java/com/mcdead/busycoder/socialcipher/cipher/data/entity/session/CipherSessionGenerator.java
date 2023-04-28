package com.mcdead.busycoder.socialcipher.cipher.data.entity.session;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInitGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInit;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.utility.CipherSessionUtility;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;

import javax.crypto.KeyAgreement;

public class CipherSessionGenerator {
    public static CipherSession generateCipherSession(
            final long localPeerId,
            final KeyPair keyPair,
            final KeyAgreement keyAgreement,
            final List<Long> userPeerIdList)
    {
        if (userPeerIdList == null || localPeerId == 0)
            return null;
        if (userPeerIdList.isEmpty())
            return null;

        HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap =
                generateSessionSideIdUserPeerIdHashMap(userPeerIdList);

        return generateCipherSessionBasis(localPeerId, keyPair, keyAgreement, sessionSideIdUserPeerIdHashMap);
    }

    public static CipherSession generateCipherSessionWithSessionSideIdUserPeerIdPairList(
            final long localPeerId,
            final KeyPair keyPair,
            final KeyAgreement keyAgreement,
            final List<Pair<Long, Integer>> sessionUserPeerIdSideIdPairList)
    {
        if (sessionUserPeerIdSideIdPairList == null || localPeerId == 0)
            return null;
        if (sessionUserPeerIdSideIdPairList.isEmpty())
            return null;

        HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap =
                new HashMap<>();

        for (final Pair<Long, Integer> sessionUserPeerIdSideIdPair :
                sessionUserPeerIdSideIdPairList)
        {
            sessionSideIdUserPeerIdHashMap.put(
                    sessionUserPeerIdSideIdPair.second,
                    sessionUserPeerIdSideIdPair.first);
        }

        return generateCipherSessionBasis(localPeerId, keyPair, keyAgreement, sessionSideIdUserPeerIdHashMap);
    }

    private static CipherSession generateCipherSessionBasis(
            final long localPeerId,
            final KeyPair keyPair,
            final KeyAgreement keyAgreement,
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        // todo: generating CipherSessionStateInit..

        CipherSessionStateInit initState =
                CipherSessionStateInitGenerator.
                        generateCipherSessionStateInit(
                                keyPair.getPrivate(),
                                keyPair.getPublic(),
                                keyAgreement,
                                sessionSideIdUserPeerIdHashMap);

        if (initState == null) return null;

        // todo: getting local side it by localPeerId..

        int sideId =
                CipherSessionUtility.getSideIdByLocalPeerIdFromHashMap(
                        localPeerId, sessionSideIdUserPeerIdHashMap);

        if (sideId < 0) return null;

        // todo: generating CipherSession..

        return new CipherSession(
                initState,
                sideId,
                sessionSideIdUserPeerIdHashMap);
    }

    private static HashMap<Integer, Long> generateSessionSideIdUserPeerIdHashMap(
            final List<Long> userPeerIdList)
    {

    }
}
