package com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size;

public enum AttachmentSize {
    SMALL(1), STANDARD(2);

    private int m_id = 0;

    private AttachmentSize(final int id) {
        m_id = id;
    }

    public int getId() {
        return m_id;
    }

    public static AttachmentSize getSizeById(
            final int id)
    {
        if (id <= 0) return null;

        for (final AttachmentSize size : AttachmentSize.values())
            if (size.m_id == id) return size;

        return null;
    }
}
