package com.mcdead.busycoder.socialcipher.cipher.data.entity.key;

public enum CipherKeySize {
    KEY_128(1, 128), KEY_192(2, 192), KEY_256(3, 256);

    final private int m_id;
    final private int m_size;

    private CipherKeySize(
            final int id,
            final int size)
    {
        m_id = id;
        m_size = size;
    }

    public int getIntSize() {
        return m_size;
    }

    public int getId() {
        return m_id;
    }

    public static CipherKeySize getCipherKeySizeById(final int id) {
        if (id <= 0) return null;

        for (final CipherKeySize cipherKeySize : CipherKeySize.values())
            if (cipherKeySize.m_id == id)
                return cipherKeySize;

        return null;
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
