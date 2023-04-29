package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.util.HashMap;

public class CipherCommandDataInitRoute extends CipherCommandData {
    final private HashMap<Integer, Pair<Integer, byte[]>> m_sideIdRouteIdDataHashMap;

    private CipherCommandDataInitRoute(
            final HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap)
    {
        m_sideIdRouteIdDataHashMap = sideIdRouteIdDataHashMap;
    }

    public static CipherCommandDataInitRoute getInstance(
            final HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap)
    {
        if (sideIdRouteIdDataHashMap == null) return null;
        if (sideIdRouteIdDataHashMap.isEmpty()) return null;

        return new CipherCommandDataInitRoute(sideIdRouteIdDataHashMap);
    }

    public HashMap<Integer, Pair<Integer, byte[]>> getSideIdRouteIdDataHashMap() {
        return m_sideIdRouteIdDataHashMap;
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_ROUTE;
    }
}
