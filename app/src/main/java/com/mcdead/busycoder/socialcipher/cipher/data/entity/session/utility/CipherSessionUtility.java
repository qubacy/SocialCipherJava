package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.utility;

import java.util.HashMap;
import java.util.Map;

public class CipherSessionUtility {
    public static int getSideIdByLocalPeerIdFromHashMap(
            final long localPeerId,
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        if (localPeerId == 0) return -1;

        for (final Map.Entry entry : sessionSideIdUserPeerIdHashMap.entrySet()) {
            if ((Long)entry.getValue() == localPeerId)
                return (Integer)entry.getKey();
        }

        return -1;
    }
}
