package com.mcdead.busycoder.socialcipher.client.data.entity.attachment.data;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

public abstract class AttachmentData {
    final protected String m_name;
    final protected String m_extension;
    final protected byte[] m_bytes;

    protected AttachmentData(
            final String name,
            final String extension,
            final byte[] bytes)
    {
        m_name = name;
        m_extension = extension;
        m_bytes = bytes;
    }

    public String getName() {
        return m_name;
    }

    public String getExtension() {
        return m_extension;
    }

    public byte[] getBytes() {
        return m_bytes;
    }

    public abstract AttachmentType getType();
}
