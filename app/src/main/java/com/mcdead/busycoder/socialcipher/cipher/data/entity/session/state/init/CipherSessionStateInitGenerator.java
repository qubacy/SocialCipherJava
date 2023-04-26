package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.KeyAgreement;

public class CipherSessionStateInitGenerator {
    public static CipherSessionStateInit generateCipherSessionStateInit(
            final PrivateKey localPrivateKey,
            final PublicKey publicKey,
            final KeyAgreement keyAgreement,
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        if (sessionSideIdUserPeerIdHashMap == null)
            return null;
        if (sessionSideIdUserPeerIdHashMap.isEmpty())
            return null;

        List<CipherSessionInitRoute> initRouteList = generateRouteList();

        // todo: filling route list..

        return new CipherSessionStateInit(
                localPrivateKey,
                publicKey,
                keyAgreement,
                initRouteList);
    }

    private static List<CipherSessionInitRoute> generateRouteList(
            final HashMap<Integer, Long> sessionSideIdUserPeerIdHashMap)
    {
        List<CipherSessionInitRoute> initRouteList = new LinkedList<>();




    }
}
