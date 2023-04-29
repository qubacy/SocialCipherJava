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

        HashMap<Long, Integer> userPeerIdSessionSideIdHashMap =
                generateSessionSideIdUserPeerIdHashMap(localPeerId, userPeerIdList);

        return generateCipherSessionBasis(localPeerId, keyPair, keyAgreement, userPeerIdSessionSideIdHashMap);
    }

    public static CipherSession generateCipherSessionWithSessionSideIdUserPeerIdPairList(
            final long localPeerId,
            final KeyPair keyPair,
            final KeyAgreement keyAgreement,
            final HashMap<Long, Integer> sessionUserPeerIdSideIdHashMap)
    {
        if (sessionUserPeerIdSideIdHashMap == null || localPeerId == 0)
            return null;
        if (sessionUserPeerIdSideIdHashMap.isEmpty())
            return null;

        return generateCipherSessionBasis(localPeerId, keyPair, keyAgreement, sessionUserPeerIdSideIdHashMap);
    }

    private static CipherSession generateCipherSessionBasis(
            final long localPeerId,
            final KeyPair keyPair,
            final KeyAgreement keyAgreement,
            final HashMap<Long, Integer> userPeerIdSessionSideIdHashMap)
    {
        CipherSessionStateInit initState =
                CipherSessionStateInitGenerator.
                        generateCipherSessionStateInit(
                                keyPair.getPrivate(),
                                keyPair.getPublic(),
                                keyAgreement,
                                userPeerIdSessionSideIdHashMap);

        if (initState == null) return null;

        int sideId = userPeerIdSessionSideIdHashMap.get(localPeerId);

//        int sideId =
//                CipherSessionUtility.getSideIdByLocalPeerIdFromHashMap(
//                        localPeerId, userPeerIdSessionSideIdHashMap);

        if (sideId < 0) return null;

        return new CipherSession(
                initState,
                sideId,
                userPeerIdSessionSideIdHashMap);
    }

    private static HashMap<Long, Integer> generateSessionSideIdUserPeerIdHashMap(
            final long localPeerId,
            final List<Long> userPeerIdList)
    {
        HashMap<Long, Integer> userPeerIdSessionSideIdHashMap =
                new HashMap<>();

        userPeerIdSessionSideIdHashMap.put(localPeerId, 0);

        for (int i = 0; i < userPeerIdList.size(); ++i)
            userPeerIdSessionSideIdHashMap.put(userPeerIdList.get(i), i);

        return userPeerIdSessionSideIdHashMap;
    }
}
