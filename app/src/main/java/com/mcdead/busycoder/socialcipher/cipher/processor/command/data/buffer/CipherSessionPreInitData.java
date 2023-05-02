package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

public class CipherSessionPreInitData {
    final private long m_startTimeMillisecond;
    final private long m_initializerPeerId;

    final private CipherSessionInitBuffer m_buffer;

    public CipherSessionPreInitData(
            final long startTimeMillisecond,
            final long initializerPeerId,
            final CipherSessionInitBuffer buffer)
    {
        m_startTimeMillisecond = startTimeMillisecond;
        m_initializerPeerId = initializerPeerId;

        m_buffer = buffer;
    }

    public long getStartTime() {
        return m_startTimeMillisecond;
    }

    public long getInitializerPeerId() {
        return m_initializerPeerId;
    }

    public CipherSessionInitBuffer getBuffer() {
        return m_buffer;
    }
}
