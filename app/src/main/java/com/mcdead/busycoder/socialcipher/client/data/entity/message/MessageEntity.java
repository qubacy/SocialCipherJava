package com.mcdead.busycoder.socialcipher.client.data.entity.message;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageEntity {
    private long m_id = 0;
    private long m_fromPeerId = 0;
    private String m_message = null;
    private long m_timestamp = 0;
    private boolean m_isCiphered = false;

    private List<ResponseAttachmentInterface> m_attachmentToLoadList = null;
    private volatile List<AttachmentEntityBase> m_attachmentsList = null;

    protected MessageEntity(
            final long id,
            final long fromPeerId,
            final String message,
            final long timestamp,
            final boolean isCiphered,
            final List<ResponseAttachmentInterface> attachmentsToLoadList)
    {
        m_id = id;
        m_fromPeerId = fromPeerId;
        m_message = message;
        m_timestamp = timestamp;
        m_isCiphered = isCiphered;

        m_attachmentToLoadList = attachmentsToLoadList;
        m_attachmentsList = new ArrayList<>();
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

    public boolean isCiphered() {
        return m_isCiphered;
    }

    public List<AttachmentEntityBase> getAttachments() {
        return m_attachmentsList;
    }

    public boolean setAttachments(final List<AttachmentEntityBase> attachmentsList) {
        if (attachmentsList == null) return false;

        synchronized (m_attachmentsList) {
            if (!m_attachmentsList.isEmpty()) return false;

            m_attachmentsList = attachmentsList;

            m_attachmentToLoadList.clear();
        }

        return true;
    }

    public boolean setCiphered() {
        if (m_isCiphered) return false;

        m_isCiphered = true;

        return true;
    }

    public List<ResponseAttachmentInterface> getAttachmentToLoad() {
        return m_attachmentToLoadList;
    }

    public boolean areAttachmentsLoaded() {
        return (m_attachmentToLoadList == null
                ? true
                : m_attachmentToLoadList.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MessageEntity that = (MessageEntity) o;

        return m_id == that.m_id &&
                m_fromPeerId == that.m_fromPeerId &&
                m_timestamp == that.m_timestamp &&
                m_isCiphered == that.m_isCiphered &&
                Objects.equals(m_message, that.m_message) &&
                Objects.equals(m_attachmentToLoadList, that.m_attachmentToLoadList) &&
                Objects.equals(m_attachmentsList, that.m_attachmentsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_id, m_fromPeerId, m_message, m_timestamp, m_isCiphered, m_attachmentToLoadList, m_attachmentsList);
    }
}
