package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseAttachmentInterface;

import java.io.Serializable;

public abstract class ResponseAttachmentBase implements Serializable, ResponseAttachmentInterface {
    protected String m_attachmentType = null;

    public ResponseAttachmentBase() {

    }

    public ResponseAttachmentBase(final String attachmentType)
    {
        m_attachmentType = attachmentType;
    }

    public abstract ResponseAttachmentType getResponseAttachmentType();

    public String getAttachmentType() {
        return m_attachmentType;
    }
}
