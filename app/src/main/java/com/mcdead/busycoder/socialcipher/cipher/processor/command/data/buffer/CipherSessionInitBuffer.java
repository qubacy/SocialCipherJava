package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;

public class CipherSessionInitBuffer {
    final private CipherConfiguration m_cipherConfiguration;

    final private long m_initializerPeerId;

    public CipherSessionInitBuffer(
            final CipherConfiguration cipherConfiguration,
            final long initializerPeerId)
    {
        m_cipherConfiguration = cipherConfiguration;

        m_initializerPeerId = initializerPeerId;
    }

    public CipherConfiguration getCipherConfiguration() {
        return m_cipherConfiguration;
    }

    public long getInitializerPeerId() {
        return m_initializerPeerId;
    }
}
