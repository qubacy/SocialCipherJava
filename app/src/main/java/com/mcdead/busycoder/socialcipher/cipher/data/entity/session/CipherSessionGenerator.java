package com.mcdead.busycoder.socialcipher.cipher.data.entity.session;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInitGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInit;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.utility.CipherSessionUtility;

import java.util.HashMap;

public class CipherSessionGenerator {
    public static CipherSession generateCipherSession(
            final long localPeerId,
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        if (sessionSideIdUserPeerIdHashMap == null || localPeerId == 0)
            return null;
        if (sessionSideIdUserPeerIdHashMap.isEmpty())
            return null;

        // todo: generating CipherSessionStateInit..

        CipherSessionStateInit initState =
                CipherSessionStateInitGenerator.
                        generateCipherSessionStateInit(
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
}
