package com.mcdead.busycoder.socialcipher.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;

import java.net.URI;

public abstract class AttachmentEntityBase {
    private String m_id = null;
    private URI m_uri = null;

    public AttachmentEntityBase(final String id,
                                final URI uri)
    {
        m_id = id;
        m_uri = uri;
    }

    public String getId() {
        return m_id;
    }

    public URI getURI() {
        return m_uri;
    }

    public abstract AttachmentType getType();
}
