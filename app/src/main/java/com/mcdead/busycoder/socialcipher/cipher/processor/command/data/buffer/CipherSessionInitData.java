package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

public class CipherSessionInitData {
    final private long m_startTimeMillisecond;
    final private long m_initializerPeerId;
    private boolean m_isPreInitPassed;

    final private CipherSessionInitBuffer m_buffer;

    public CipherSessionInitData(
            final long startTimeMillisecond,
            final long initializerPeerId,
            final CipherSessionInitBuffer buffer)
    {
        m_startTimeMillisecond = startTimeMillisecond;
        m_initializerPeerId = initializerPeerId;
        m_isPreInitPassed = false;

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

    public CipherSessionInitBuffer getBuffer() {
        return m_buffer;
    }

    public void setPreInitPassed() {
        m_isPreInitPassed = true;
    }
}
