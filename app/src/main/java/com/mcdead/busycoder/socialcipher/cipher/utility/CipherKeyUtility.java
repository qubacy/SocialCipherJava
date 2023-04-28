package com.mcdead.busycoder.socialcipher.cipher.utility;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class CipherKeyUtility {
    public static PublicKey generatePublicKeyWithBytes(
            final String algorithm,
            final byte[] publicKeyBytes)
    {
        if (publicKeyBytes == null) return null;
        if (publicKeyBytes.length <= 0) return null;

        PublicKey publicKey = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        return publicKey;
    }
}
