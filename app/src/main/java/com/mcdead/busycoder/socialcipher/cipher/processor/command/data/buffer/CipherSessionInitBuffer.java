package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;

public class CipherSessionInitBuffer {
    final private CipherConfiguration m_cipherConfiguration;

    public CipherSessionInitBuffer(
            final CipherConfiguration cipherConfiguration)
    {
        m_cipherConfiguration = cipherConfiguration;
    }

    public CipherConfiguration getCipherConfiguration() {
        return m_cipherConfiguration;
    }
}
