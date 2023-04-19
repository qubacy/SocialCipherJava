package com.mcdead.busycoder.socialcipher.api;

public enum APIType {
    VK(1);

    private int m_id = 0;

    private APIType(final int id) {
        m_id = id;
    }

    public int getId() {
        return m_id;
    }

    public static APIType getAPITypeById(final int id) {
        if (id == 0) return null;

        for (final APIType type : APIType.values())
            if (type.m_id == id) return type;

        return null;
    }
}
