package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

public class AttachmentPickerViewModel extends ViewModel {
    private AttachmentType m_attachmentType = null;

    public AttachmentPickerViewModel() {
        super();
    }

    public boolean setAttachmentType(final AttachmentType attachmentType) {
        if (attachmentType == null)
            return false;

        m_attachmentType = attachmentType;

        return true;
    }

    public AttachmentType getAttachmentType() {
        return m_attachmentType;
    }

    public boolean isInitialized() {
        return (m_attachmentType != null);
    }
}
