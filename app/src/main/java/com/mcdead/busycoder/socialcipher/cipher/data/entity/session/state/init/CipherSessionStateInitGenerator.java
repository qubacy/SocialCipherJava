package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CipherSessionStateInitGenerator {
    public static CipherSessionStateInit generateCipherSessionStateInit(
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        if (sessionSideIdUserPeerIdHashMap == null)
            return null;
        if (sessionSideIdUserPeerIdHashMap.isEmpty())
            return null;

        List<CipherSessionInitRoute> initRouteList = new LinkedList<>();

        // todo: filling route list..

        return new CipherSessionStateInit(initRouteList);
    }
}
