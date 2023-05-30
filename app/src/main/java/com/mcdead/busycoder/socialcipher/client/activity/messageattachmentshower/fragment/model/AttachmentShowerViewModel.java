package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.fragment.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

public class AttachmentShowerViewModel extends ViewModel {
    private AttachmentEntityBase m_attachment = null;

    public AttachmentShowerViewModel() {

    }

    public boolean setAttachment(final AttachmentEntityBase attachmentEntity) {
        if (attachmentEntity == null || m_attachment != null)
            return false;

        m_attachment = attachmentEntity;

        return true;
    }

    public AttachmentEntityBase getAttachment() {
        return m_attachment;
    }

    public boolean isInitialized() {
        return (m_attachment != null);
    }
}
