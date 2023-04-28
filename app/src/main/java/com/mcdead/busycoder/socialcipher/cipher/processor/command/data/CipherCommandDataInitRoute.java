package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.util.HashMap;

public class CipherCommandDataInitRoute extends CipherCommandData {
    final private HashMap<Integer, byte[]> m_routeIdDataHashMap;

    private CipherCommandDataInitRoute(
            final HashMap<Integer, byte[]> routeIdDataHashMap)
    {
        m_routeIdDataHashMap = routeIdDataHashMap;
    }

    public static CipherCommandDataInitRoute getInstance(
            final HashMap<Integer, byte[]> routeIdDataHashMap)
    {
        if (routeIdDataHashMap == null) return null;
        if (routeIdDataHashMap.isEmpty()) return null;

        return new CipherCommandDataInitRoute(routeIdDataHashMap);
    }

    public HashMap<Integer, byte[]> getRouteIdDataHashMap() {
        return m_routeIdDataHashMap;
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_ROUTE;
    }
}
