package com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype;

import java.io.Serializable;
import java.util.Objects;

public enum AttachmentType implements Serializable {
    IMAGE(1, new String[]{"png", "jpg", "jpeg"}),
    VIDEO(2, new String[]{"mp4", "webm"}),
    AUDIO(3, new String[]{"mp3"}),
    DOC(4, new String[]{});

    private int m_id = 0;
    private String[] m_extensions = null;

    private AttachmentType(int id, String[] extensions) {
        m_id = id;
        m_extensions = extensions;
    }

    public static AttachmentType getTypeByExtension(final String extension) {
        if (extension == null) return null;

        AttachmentType[] attachmentTypes = AttachmentType.values();

        for (final AttachmentType attachmentType : attachmentTypes) {
            for (final String attachmentExtension : attachmentType.m_extensions)
                if (Objects.equals(attachmentExtension, extension))
                    return attachmentType;
        }

        return DOC;
    }

    public static AttachmentType getTypeById(final int id) {
        if (id <= 0) return null;

        AttachmentType[] attachmentTypes = AttachmentType.values();

        for (final AttachmentType attachmentType : attachmentTypes)
            if (attachmentType.m_id == id) return attachmentType;

        return null;
    }

    public int getId() {
        return m_id;
    }
}
