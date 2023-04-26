package com.mcdead.busycoder.socialcipher.cipher.processor.command;

public enum CipherCommandType {
    CIPHER_SESSION_INIT_REQUEST(1),
    CIPHER_SESSION_INIT_ACCEPT(2),
    CIPHER_SESSION_INIT_COMPLETED(3),
    CIPHER_SESSION_INIT_ROUTE(4);

    final private int m_id;

    private CipherCommandType(
            final int id)
    {
        m_id = id;
    }

    public int getId() {
        return m_id;
    }

    public static CipherCommandType getCommandTypeById(final int id) {
        if (id == 0) return null;

        for (final CipherCommandType command : CipherCommandType.values())
            if (command.m_id == id)
                return command;

        return null;
    }
}
