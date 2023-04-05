package com.mcdead.busycoder.socialcipher.api.vk;

import java.util.Objects;

public enum VKAttachmentType {
    PHOTO("photo"), DOC("doc"), STICKER("sticker");

    private String m_type = null;

    private VKAttachmentType(final String type) {
        m_type = type;
    }

    public String getType() {
        return m_type;
    }

    public static VKAttachmentType getTypeByString(final String type) {
        for (final VKAttachmentType curType : VKAttachmentType.values())
            if (Objects.equals(curType.m_type, type))
                return curType;

        return null;
    }
}
