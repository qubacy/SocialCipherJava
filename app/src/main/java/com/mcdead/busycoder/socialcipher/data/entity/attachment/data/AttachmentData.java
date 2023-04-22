package com.mcdead.busycoder.socialcipher.data.entity.attachment.data;

public class AttachmentData {
    private String m_name = null;
    private String m_extension = null;
    private byte[] m_bytes = null;

    public AttachmentData(
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

    public boolean isValid() {
        if (m_name == null || m_extension == null || m_bytes == null)
            return false;

        return !(m_name.isEmpty() || m_bytes.length <= 0);
    }
}
