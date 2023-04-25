package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration;

public enum CipherPadding {
    NO_PADDING(1, "NoPadding"),
    PKCS5(2, "PKCS5Padding");

    final private int m_id;
    final private String m_name;

    private CipherPadding(
            final int id,
            final String name)
    {
        m_id = id;
        m_name = name;
    }

    public int getId() {
        return m_id;
    }

    public String getName() {
        return m_name;
    }

    public static CipherPadding getPaddingById(
            final int id)
    {
        if (id <= 0) return null;

        for (final CipherPadding cipherPadding : CipherPadding.values()) {
            if (cipherPadding.m_id == id)
                return cipherPadding;
        }

        return null;
    }
}
