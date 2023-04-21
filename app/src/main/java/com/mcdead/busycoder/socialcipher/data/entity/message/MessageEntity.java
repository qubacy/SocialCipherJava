package com.mcdead.busycoder.socialcipher.data.entity.message;

import com.mcdead.busycoder.socialcipher.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;

import java.util.ArrayList;
import java.util.List;

public class MessageEntity {
    private long m_id = 0;
    private long m_fromPeerId = 0;
    private String m_message = null;
    private long m_timestamp = 0;

    private List<ResponseAttachmentInterface> m_attachmentToLoadList = null;
    private volatile List<AttachmentEntityBase> m_attachmentsList = null;

    protected MessageEntity(
            final long id,
            final long fromPeerId,
            final String message,
            final long timestamp,
            final List<ResponseAttachmentInterface> attachmentsToLoadList)
    {
        m_id = id;
        m_fromPeerId = fromPeerId;
        m_message = message;
        m_timestamp = timestamp;

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

//    public boolean addAttachment(AttachmentEntityBase attachment) {
//        if (attachment == null) return false;
//
//        m_attachmentsList.add(attachment);
//
//        return false;
//    }

    public List<ResponseAttachmentInterface> getAttachmentToLoad() {
        return m_attachmentToLoadList;
    }

    public boolean areAttachmentsLoaded() {
        return (m_attachmentToLoadList == null
                ? true
                : m_attachmentToLoadList.isEmpty());
    }
}
