package com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.processor.data;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

import java.util.List;
import java.util.Objects;

public class AttachmentProcessingResult {
    final private List<AttachmentEntityBase> m_loadedAttachmentList;
    final private boolean m_isCiphered;

    public AttachmentProcessingResult(
            final List<AttachmentEntityBase> loadedAttachmentList,
            final boolean isCiphered)
    {
        m_loadedAttachmentList = loadedAttachmentList;
        m_isCiphered = isCiphered;
    }

    public List<AttachmentEntityBase> getLoadedAttachmentList() {
        return m_loadedAttachmentList;
    }

    public boolean isCiphered() {
        return m_isCiphered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AttachmentProcessingResult that = (AttachmentProcessingResult) o;

        return m_isCiphered == that.m_isCiphered &&
                Objects.equals(m_loadedAttachmentList, that.m_loadedAttachmentList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_loadedAttachmentList, m_isCiphered);
    }
}
