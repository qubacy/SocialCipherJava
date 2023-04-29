package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

public class CipherSessionPreInitData {
    final private long m_startTimeMillisecond;

    final private CipherSessionInitBuffer m_buffer;

    public CipherSessionPreInitData(
            final long startTimeMillisecond,
            final CipherSessionInitBuffer buffer)
    {
        m_startTimeMillisecond = startTimeMillisecond;

        m_buffer = buffer;
    }

    public long getStartTime() {
        return m_startTimeMillisecond;
    }

    public CipherSessionInitBuffer getBuffer() {
        return m_buffer;
    }
}
