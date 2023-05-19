package com.mcdead.busycoder.socialcipher.client.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AttachmentEntityBase that = (AttachmentEntityBase) o;

        for (final Map.Entry<AttachmentSize, URI> entry : m_sizeUriHashMap.entrySet()) {
            if (!that.m_sizeUriHashMap.containsKey(entry.getKey()))
                return false;
            if (!that.m_sizeUriHashMap.get(entry.getKey()).equals(entry.getValue()))
                return false;
        }

        return Objects.equals(m_id, that.m_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_id, m_sizeUriHashMap);
    }
}
