package com.mcdead.busycoder.socialcipher.command;

public enum CommandCategory {
    CIPHER(1);

    final private int m_id;

    private CommandCategory(
            final int id)
    {
        m_id = id;
    }

    public int getId() {
        return m_id;
    }

    public static CommandCategory getCategoryById(final int id) {
        if (id == 0) return null;

        for (final CommandCategory commandCategory : CommandCategory.values())
            if (commandCategory.m_id == id)
                return commandCategory;

        return null;
    }
}
