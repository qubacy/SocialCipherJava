package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;

public class CipherSessionInitData {
    final private long m_startTimeMillisecond;
    final private long m_initializerPeerId;

    private boolean m_isPreInitPassed;
    private boolean m_isInitialized;

    final private CipherConfiguration m_cipherConfiguration;

    public CipherSessionInitData(
            final long startTimeMillisecond,
            final long initializerPeerId,
            final CipherConfiguration cipherConfiguration)
    {
        m_startTimeMillisecond = startTimeMillisecond;
        m_initializerPeerId = initializerPeerId;

        m_isPreInitPassed = false;
        m_isInitialized = false;

        m_cipherConfiguration = cipherConfiguration;
    }

    public long getStartTime() {
        return m_startTimeMillisecond;
    }

    public long getInitializerPeerId() {
        return m_initializerPeerId;
    }

    public boolean isPreInitPassed() {
        return m_isPreInitPassed;
    }

    public boolean isInitialized() {
        return m_isInitialized;
    }

    public CipherConfiguration getCipherConfiguration() {
        return m_cipherConfiguration;
    }

    public void setPreInitPassed() {
        m_isPreInitPassed = true;
    }

    public void setInitialized() {
        m_isInitialized = true;
    }
}
