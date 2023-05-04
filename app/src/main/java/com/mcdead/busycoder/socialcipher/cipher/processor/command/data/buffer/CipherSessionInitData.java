package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;

import java.util.List;

public class CipherSessionInitData {
    final private long m_startTimeMillisecond;
    final private long m_initializerPeerId;

    private boolean m_isPreInitPassed;
    private boolean m_isInitialized;

    final private CipherSessionInitBuffer m_buffer;

    public CipherSessionInitData(
            final long startTimeMillisecond,
            final long initializerPeerId,
            final CipherSessionInitBuffer buffer)
    {
        m_startTimeMillisecond = startTimeMillisecond;
        m_initializerPeerId = initializerPeerId;

        m_isPreInitPassed = false;
        m_isInitialized = false;

        m_buffer = buffer;
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

    public CipherSessionInitBuffer getBuffer() {
        return m_buffer;
    }

    public void setPreInitPassed() {
        m_isPreInitPassed = true;
    }

    public void setInitialized() {
        m_isInitialized = true;
    }
}
