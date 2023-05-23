package com.mcdead.busycoder.socialcipher.client.data.entity.attachment.data;

public class AttachmentData {
    final private String m_name;
    final private String m_extension;
    final private byte[] m_bytes;

    protected AttachmentData(
            final String name,
            final String extension,
            final byte[] bytes)
    {
        m_name = name;
        m_extension = extension;
        m_bytes = bytes;
    }

    public static AttachmentData getInstance(
            final String name,
            final String extension,
            final byte[] bytes)
    {
        if (name == null || extension == null || bytes == null)
            return null;
        if ((name.isEmpty() && extension.isEmpty()) || bytes.length <= 0)
            return null;

        return new AttachmentData(name, extension, bytes);
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
}
