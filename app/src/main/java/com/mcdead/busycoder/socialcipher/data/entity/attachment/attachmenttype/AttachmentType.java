package com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype;

import java.util.Objects;

public enum AttachmentType {
    IMAGE(new String[]{"png", "jpg", "jpeg"}),
    VIDEO(new String[]{"mp4", "webm"}),
    AUDIO(new String[]{"mp3"}),
    DOC(new String[]{});

    private String[] m_extensions = null;

    private AttachmentType(String[] extensions) {
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
}
