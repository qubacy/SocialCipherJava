package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import androidx.core.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CipherCommandDataInitRoute that = (CipherCommandDataInitRoute) o;

        for (final Map.Entry<Integer, Pair<Integer, byte[]>> entry :
                that.m_sideIdRouteIdDataHashMap.entrySet())
        {
            Pair<Integer, byte[]> localData =
                    m_sideIdRouteIdDataHashMap.get(entry.getKey());

            if (localData == null) return false;
            if (!localData.first.equals(entry.getValue().first) ||
                !Arrays.equals(localData.second, entry.getValue().second))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_sideIdRouteIdDataHashMap);
    }
}
