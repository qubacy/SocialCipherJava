package com.mcdead.busycoder.socialcipher.cipher.utility;

import com.mcdead.busycoder.socialcipher.cipher.CipherContext;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class CipherKeyUtility {
    public static final int C_KEY_SIZE_BITS = 2048;

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

    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CipherContext.C_ALGORITHM);

        keyPairGenerator.initialize(C_KEY_SIZE_BITS);

        return keyPairGenerator.generateKeyPair();
    }

    public static KeyPair generateKeyPairWithPublicKey(final PublicKey publicKey)
            throws
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException
    {
        DHParameterSpec dhParameterSpec = ((DHPublicKey) publicKey).getParams();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CipherContext.C_ALGORITHM);

        keyPairGenerator.initialize(dhParameterSpec);

        return keyPairGenerator.generateKeyPair();
    }

    public static KeyAgreement generateKeyAgreement(
            final PrivateKey localPrivateKey)
            throws
            NoSuchAlgorithmException,
            InvalidKeyException
    {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(CipherContext.C_ALGORITHM);

        keyAgreement.init(localPrivateKey);

        return keyAgreement;
    }
}
