package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration;

public enum CipherAlgorithm {
    AES(1, "AES");

    final private int m_id;
    final private String m_name;

    private CipherAlgorithm(
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

    public static CipherAlgorithm getAlgorithmById(
            final int id)
    {
        if (id <= 0) return null;

        for (final CipherAlgorithm cipherAlgorithm : CipherAlgorithm.values()) {
            if (cipherAlgorithm.m_id == id)
                return cipherAlgorithm;
        }

        return null;
    }
}
