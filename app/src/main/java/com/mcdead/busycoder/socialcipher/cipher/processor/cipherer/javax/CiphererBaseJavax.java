package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.javax;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererBase;

import java.security.AlgorithmParameters;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class CiphererBaseJavax extends CiphererBase {
    protected Cipher m_cipher = null;

    protected CiphererBaseJavax(
            final CipherKey cipherKey,
            final Cipher cipher)
    {
        super(cipherKey);

        m_cipher = cipher;
    }

    protected boolean init(final boolean isEncryption,
                           final AlgorithmParameters params)
    {
        byte[] rawKey = m_key.getBytes();

        if (rawKey.length <= 0) return false;

        SecretKeySpec encodedKeySpec = new SecretKeySpec(rawKey, getAlgorithm().getName());

        try {
            if (isEncryption)
                m_cipher.init(Cipher.ENCRYPT_MODE, encodedKeySpec);
            else
                m_cipher.init(Cipher.ENCRYPT_MODE, encodedKeySpec, params);

        } catch (Throwable e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public byte[] encryptBytes(final byte[] dataToEncrypt) {
        if (dataToEncrypt == null) return null;
        if (dataToEncrypt.length <= 0)
            return null;

        if (!init(true,  null))
            return null;

        byte[] cipheredBytes = null;

        try {
            byte[] cipheredBytesBuffer = m_cipher.doFinal(dataToEncrypt);
            byte[] ivBytes = m_cipher.getIV();

            if (cipheredBytesBuffer == null)
                return null;
            if (cipheredBytesBuffer.length < dataToEncrypt.length)
                return null;

            cipheredBytes = new byte[ivBytes.length + cipheredBytesBuffer.length];

            System.arraycopy(ivBytes, 0, cipheredBytes, 0, ivBytes.length);
            System.arraycopy(cipheredBytesBuffer, 0, cipheredBytes, ivBytes.length, cipheredBytesBuffer.length);

        } catch (Throwable e) {
            e.printStackTrace();

            return null;
        }

        return cipheredBytes;
    }

    @Override
    public byte[] decryptBytes(final byte[] dataToDecrypt) {
        if (dataToDecrypt == null) return null;
        if (dataToDecrypt.length <= 0)
            return null;

        int ivBytesCount = getIVSize();

        byte[] ivBytesReceived = Arrays.copyOfRange(dataToDecrypt, 0, ivBytesCount);
        byte[] bytesToDecipher = Arrays.copyOfRange(dataToDecrypt, ivBytesCount, dataToDecrypt.length);

        byte[] decipheredBytes = null;

        try {
            AlgorithmParameters params = AlgorithmParameters.getInstance(getAlgorithm().getName());

            params.init(new IvParameterSpec(ivBytesReceived));

            if (!init(false,  params))
                return null;

            decipheredBytes = m_cipher.doFinal(bytesToDecipher);

        } catch (Throwable e) {
            e.printStackTrace();

            return null;
        }

        return decipheredBytes;
    }
}

