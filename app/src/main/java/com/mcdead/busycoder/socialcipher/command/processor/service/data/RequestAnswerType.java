package com.mcdead.busycoder.socialcipher.command.processor.service.data;

public enum RequestAnswerType {
    SETTING_CIPHER_SESSION(1, "Would you like to accept setting ciphering session? Initializer is %s!");

    final private int m_id;
    final private String m_text;

    private RequestAnswerType(
            final int id,
            final String text)
    {
        m_id = id;
        m_text = text;
    }

    public int getId() {
        return m_id;
    }

    public String getText() {
        return m_text;
    }

    public static RequestAnswerType getRequestTypeById(
            final int id)
    {
        if (id <= 0) return null;

        for (final RequestAnswerType type : RequestAnswerType.values())
            if (type.m_id == id) return type;

        return null;
    }
}
