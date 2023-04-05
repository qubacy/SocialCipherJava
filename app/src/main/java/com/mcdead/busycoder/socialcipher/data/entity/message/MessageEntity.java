package com.mcdead.busycoder.socialcipher.data.entity.message;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;

import java.util.List;

public class MessageEntity {
    private long m_id = 0;
    private long m_fromPeerId = 0;
    private String m_message = null;
    private long m_timestamp = 0;

    private List<AttachmentEntityBase> m_attachmentsList = null;

    public MessageEntity(
            final long id,
            final long fromPeerId,
            final String message,
            final long timestamp,
            final List<AttachmentEntityBase> attachmentsList)
    {
        m_id = id;
        m_fromPeerId = fromPeerId;
        m_message = message;
        m_timestamp = timestamp;
        m_attachmentsList = attachmentsList;
    }

    public long getId() {
        return m_id;
    }

    public long getFromPeerId() {
        return m_fromPeerId;
    }

    public String getMessage() {
        return m_message;
    }

    public long getTimestamp() {
        return m_timestamp;
    }

    public List<AttachmentEntityBase> getAttachments() {
        return m_attachmentsList;
    }
}
