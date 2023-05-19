package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResponseAttachmentBase that = (ResponseAttachmentBase) o;

        return Objects.equals(m_attachmentType, that.m_attachmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_attachmentType);
    }
}
