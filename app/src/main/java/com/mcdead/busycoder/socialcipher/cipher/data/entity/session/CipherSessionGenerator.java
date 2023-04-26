package com.mcdead.busycoder.socialcipher.cipher.data.entity.session;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInitGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInit;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.utility.CipherSessionUtility;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.KeyAgreement;

public class CipherSessionGenerator {
    public static final String C_ALGORITHM_NAME = "DH";

    public static final int C_KEY_SIZE_BITS = 2048;

    public static CipherSession generateCipherSession(
            final long localPeerId,
            final KeyPair keyPair,
            final KeyAgreement keyAgreement,
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

    private static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(C_ALGORITHM_NAME);

        keyPairGenerator.initialize(C_KEY_SIZE_BITS);

        return keyPairGenerator.generateKeyPair();
    }

    private static KeyAgreement generateKeyAgreement(
            final PrivateKey localPrivateKey)
            throws
            NoSuchAlgorithmException,
            InvalidKeyException
    {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(C_ALGORITHM_NAME);

        keyAgreement.init(localPrivateKey);

        return keyAgreement;
    }
}
