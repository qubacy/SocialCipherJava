package com.mcdead.busycoder.socialcipher.cipher.data.entity.key;

import java.util.Arrays;

public class CipherKey {
    final private CipherKeySize m_size;
    final private byte[] m_bytes;

    protected CipherKey(
            final CipherKeySize size,
            final byte[] bytes)
    {
        m_size = size;
        m_bytes = bytes;
    }

    public CipherKeySize getSize() {
        return m_size;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(m_bytes, m_bytes.length);
    }
}
