package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;

public abstract class CiphererBase {
    protected final CipherKey m_key;

    protected CiphererBase(
            final CipherKey cipherKey)
    {
        m_key = cipherKey;
    }

    public abstract CipherAlgorithm getAlgorithm();
    public abstract int getIVSize();

    public abstract byte[] encryptBytes(final byte[] sourceBytes);
    public abstract byte[] decryptBytes(final byte[] cipheredBytes);
}
