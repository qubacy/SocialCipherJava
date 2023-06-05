package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

import java.util.List;

public class AttachmentShowerViewModel extends ViewModel {
    public static final int C_ATTACHMENT_NOT_CHOSEN = -1;

    private List<AttachmentEntityBase> m_attachmentList = null;
    private int m_curAttachmentIndex = C_ATTACHMENT_NOT_CHOSEN;
    private boolean m_isShowerVisible = false;

    public AttachmentShowerViewModel() {
        super();
    }

    public boolean setIsShowerVisible(final boolean isShowerVisible) {
        if (isShowerVisible == m_isShowerVisible) return false;

        m_isShowerVisible = isShowerVisible;

        return true;
    }

    public boolean setCurAttachmentIndex(final int index) {
        if (m_curAttachmentIndex == index || index < C_ATTACHMENT_NOT_CHOSEN)
            return false;

        m_curAttachmentIndex = index;

        return true;
    }

    public boolean setAttachmentList(
            final List<AttachmentEntityBase> attachmentList)
    {
        if (attachmentList == null || m_attachmentList != null)
            return false;

        m_attachmentList = attachmentList;

        return true;
    }

    public boolean isShowerVisible() {
        return m_isShowerVisible;
    }

    public int getCurAttachmentIndex() {
        return m_curAttachmentIndex;
    }

    public List<AttachmentEntityBase> getAttachmentList() {
        return m_attachmentList;
    }

    public AttachmentEntityBase getAttachmentByIndex(final int index) {
        if (m_attachmentList == null) return null;
        if (index < 0 || index >= m_attachmentList.size()) return null;

        return m_attachmentList.get(index);
    }

    public int getAttachmentListSize() {
        if (m_attachmentList == null) return 0;

        return m_attachmentList.size();
    }

    public boolean isInitialized() {
        return (m_attachmentList != null);
    }
}
