package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration;

public enum CipherMode {
    CTR(1, "CTR"),
    CBC(2, "CBC");

    final private int m_id;
    final private String m_name;

    private CipherMode(
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

    public static CipherMode getModeById(
            final int id)
    {
        if (id <= 0) return null;

        for (final CipherMode cipherMode : CipherMode.values()) {
            if (cipherMode.m_id == id)
                return cipherMode;
        }

        return null;
    }
}
