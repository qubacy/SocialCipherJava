package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration;

public enum CipherLibrary {
    JAVAX(1);

    final private int m_id;

    private CipherLibrary(final int id) {
        m_id = id;
    }

    public int getId() {
        return m_id;
    }

    public static CipherLibrary getLibraryById(
            final int id)
    {
        if (id <= 0) return null;

        for (final CipherLibrary cipherLibrary : CipherLibrary.values()) {
            if (cipherLibrary.m_id == id)
                return cipherLibrary;
        }

        return null;
    }
}
