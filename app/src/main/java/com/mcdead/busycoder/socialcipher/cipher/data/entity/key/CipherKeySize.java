package com.mcdead.busycoder.socialcipher.cipher.data.entity.key;

public enum CipherKeySize {
    KEY_128(128), KEY_192(192), KEY_256(256);

    final private int m_size;

    private CipherKeySize(final int size) {
        m_size = size;
    }

    public int getIntSize() {
        return m_size;
    }

    public static CipherKeySize getCipherKeySizeByInt(
            final int size)
    {
        if (size <= 0) return null;

        for (final CipherKeySize cipherKeySize : CipherKeySize.values())
            if (cipherKeySize.m_size == size)
                return cipherKeySize;

        return null;
    }
}
