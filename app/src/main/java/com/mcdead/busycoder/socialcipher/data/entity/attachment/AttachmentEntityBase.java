package com.mcdead.busycoder.socialcipher.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.type.AttachmentType;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;

public abstract class AttachmentEntityBase implements Serializable {
    private String m_id = null;
    private HashMap<AttachmentSize, URI> m_sizeUriHashMap = null;

    public AttachmentEntityBase(final String id,
                                final HashMap<AttachmentSize, URI> sizeUriHashMap)
    {
        m_id = id;
        m_sizeUriHashMap = sizeUriHashMap;
    }

    public String getId() {
        return m_id;
    }

    public URI getURIBySize(final AttachmentSize size) {
        if (!m_sizeUriHashMap.containsKey(size))
            return null;

        return m_sizeUriHashMap.get(size);
    }

    public abstract AttachmentType getType();
}
