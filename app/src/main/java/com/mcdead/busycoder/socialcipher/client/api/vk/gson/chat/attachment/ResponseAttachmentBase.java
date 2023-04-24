package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;

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
