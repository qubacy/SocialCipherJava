package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.KeyAgreement;

public class CipherSessionStateInitGenerator {
    public static CipherSessionStateInit generateCipherSessionStateInit(
            final PrivateKey localPrivateKey,
            final PublicKey publicKey,
            final KeyAgreement keyAgreement,
            final HashMap<Long, Integer> userPeerIdSessionSideIdHashMap)
    {
        if (userPeerIdSessionSideIdHashMap == null)
            return null;
        if (userPeerIdSessionSideIdHashMap.isEmpty())
            return null;

        List<CipherSessionInitRoute> initRouteList =
                generateRouteList(userPeerIdSessionSideIdHashMap.size());

        if (initRouteList == null)
            return null;

        return new CipherSessionStateInit(
                localPrivateKey,
                publicKey,
                keyAgreement,
                initRouteList);
    }

    private static List<CipherSessionInitRoute> generateRouteList(
            final int sideCount)
    {
        List<CipherSessionInitRoute> initRouteList = new LinkedList<>();

        int sideIdShiftArraySize = sideCount - 1;
        int[] sideIdShiftArray = new int[sideIdShiftArraySize];

        for (int i = 1; i < sideCount; ++i)
            sideIdShiftArray[i - 1] = i;

        // todo: creating default routes..

        for (int routeIndex = 0; routeIndex < sideCount; ++routeIndex) {
            List<Integer> sideIdRouteList = new ArrayList<>();

            for (int sideIndex = 0; sideIndex < sideCount; ++sideIndex) {
                sideIdRouteList.add(0);
                sideIdRouteList.add(sideIdShiftArray[(sideIndex + routeIndex) % (sideIdShiftArraySize - 1)]);
            }

            CipherSessionInitRoute cipherSessionInitRoute =
                    CipherSessionInitRoute.getInstance(sideIdRouteList);

            if (cipherSessionInitRoute == null)
                return null;

            initRouteList.add(cipherSessionInitRoute);
        }

        // todo: creating the last route..

        for (int i = 0; i < sideCount; ++i)
            sideIdShiftArray[i] = sideCount - 1 - i;

        return initRouteList;
    }
}
