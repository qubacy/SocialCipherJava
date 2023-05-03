package com.mcdead.busycoder.socialcipher.cipher.utility;

import com.mcdead.busycoder.socialcipher.cipher.CipherContext;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
            throws NoSuchAlgorithmException,
            NoSuchProviderException
    {
        KeyPairGenerator keyPairGenerator = generateKeyPairGenerator();

        keyPairGenerator.initialize(C_KEY_SIZE_BITS);

        return keyPairGenerator.generateKeyPair();
    }

    public static KeyPair generateKeyPairWithPublicKey(final PublicKey publicKey)
            throws
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            NoSuchProviderException
    {
        DHParameterSpec dhParameterSpec = ((DHPublicKey) publicKey).getParams();
        KeyPairGenerator keyPairGenerator = generateKeyPairGenerator();

        keyPairGenerator.initialize(dhParameterSpec);

        return keyPairGenerator.generateKeyPair();
    }

    public static KeyAgreement generateKeyAgreement(
            final PrivateKey localPrivateKey)
            throws
            NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchProviderException
    {
        KeyAgreement keyAgreement = generateKeyAgreement();

        keyAgreement.init(localPrivateKey);

        return keyAgreement;
    }

    private static KeyAgreement generateKeyAgreement()
            throws
            NoSuchAlgorithmException,
            NoSuchProviderException
    {
        return KeyAgreement.getInstance(CipherContext.C_ALGORITHM);
    }

    private static KeyPairGenerator generateKeyPairGenerator()
            throws
            NoSuchAlgorithmException,
            NoSuchProviderException
    {
        return KeyPairGenerator.getInstance(CipherContext.C_ALGORITHM);
    }
}
