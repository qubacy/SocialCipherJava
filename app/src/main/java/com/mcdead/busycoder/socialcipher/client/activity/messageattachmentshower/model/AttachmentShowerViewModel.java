package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

import java.util.List;

public class AttachmentShowerViewModel extends ViewModel {
    private List<AttachmentEntityBase> m_attachmentList = null;
    private boolean m_isShowerVisible = false;

    public AttachmentShowerViewModel() {

    }

    public boolean setIsShowerVisible(final boolean isShowerVisible) {
        if (isShowerVisible == m_isShowerVisible) return false;

        m_isShowerVisible = isShowerVisible;

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

    public List<AttachmentEntityBase> getAttachmentList() {
        return m_attachmentList;
    }

    public boolean isInitialized() {
        return (m_attachmentList != null);
    }
}
