package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.javax;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;

import javax.crypto.Cipher;

public abstract class CiphererBlockJavax extends CiphererBaseJavax {

    protected CiphererBlockJavax(
            final CipherKey cipherKey,
            final Cipher cipher)
    {
        super(cipherKey, cipher);
    }

    @Override
    public int getIVSize() {
        return m_cipher.getBlockSize();
    }
}
