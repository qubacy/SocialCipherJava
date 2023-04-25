package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.javax;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;

import javax.crypto.Cipher;

public class CiphererAESJavax extends CiphererBlockJavax {
    public CiphererAESJavax(
            final CipherKey cipherKey,
            final Cipher cipher)
    {
        super(cipherKey, cipher);
    }


    @Override
    public CipherAlgorithm getAlgorithm() {
        return CipherAlgorithm.AES;
    }
}
