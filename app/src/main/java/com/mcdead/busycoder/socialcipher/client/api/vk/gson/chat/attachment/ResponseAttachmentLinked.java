package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResponseAttachmentLinked extends ResponseAttachmentStored {
    public static final String C_URL_PROP_NAME = "url";

    protected HashMap<AttachmentSize, String> m_sizeUrlHashMap = null;

    public ResponseAttachmentLinked() {
        super();

        m_sizeUrlHashMap = new HashMap<>();
    }

    @Override
    public ResponseAttachmentType getResponseAttachmentType() {
        return ResponseAttachmentType.LINKED;
    }

    public ResponseAttachmentLinked(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId,
                                    final HashMap<AttachmentSize, String> sizeUrlHashMap)
    {
        super(attachmentType, attachmentId, attachmentOwnerId);

        m_sizeUrlHashMap = sizeUrlHashMap;
    }

    public ResponseAttachmentLinked(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId,
                                    final String attachmentAccessKey,
                                    final HashMap<AttachmentSize, String> sizeUrlHashMap)
    {
        super(attachmentType, attachmentId, attachmentOwnerId, attachmentAccessKey);

        m_sizeUrlHashMap = sizeUrlHashMap;
    }

    protected ResponseAttachmentLinked(
            final ResponseAttachmentStored basis,
            final HashMap<AttachmentSize, String> sizeUrlHashMap)
    {
        super(basis);

        m_sizeUrlHashMap = sizeUrlHashMap;
    }

    protected ResponseAttachmentLinked(
            final ResponseAttachmentLinked basis)
    {
        super(
                basis.m_attachmentType,
                basis.m_attachmentId,
                basis.m_attachmentOwnerId,
                basis.m_attachmentAccessKey);

        m_sizeUrlHashMap = basis.m_sizeUrlHashMap;
    }

    public static ResponseAttachmentLinked generateAttachmentLinkedWithFullAttachmentId(
            final String attachmentType,
            final String fullAttachmentId,
            final HashMap<AttachmentSize, String> sizeUrlHashMap)
    {
        if (sizeUrlHashMap == null) return null;
        if (sizeUrlHashMap.isEmpty()) return null;
        if (!sizeUrlHashMap.containsKey(AttachmentSize.STANDARD)) return null;

        ResponseAttachmentStored attachmentBasis =
                ResponseAttachmentStored.generateResponseAttachmentFromFullAttachmentId(
                        attachmentType, fullAttachmentId);

        if (attachmentBasis == null) return null;

        ResponseAttachmentLinked attachmentLinked =
                new ResponseAttachmentLinked(attachmentBasis, sizeUrlHashMap);

        return attachmentLinked;
    }

    public String getUrlBySize(final AttachmentSize size) {
        if (!m_sizeUrlHashMap.containsKey(size))
            return null;

        return m_sizeUrlHashMap.get(size);
    }

    public HashMap<AttachmentSize, String> getSizeUrlHashMap() {
        return m_sizeUrlHashMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        if (!super.equals(o)) return false;

        ResponseAttachmentLinked that = (ResponseAttachmentLinked) o;

        for (final Map.Entry<AttachmentSize, String> entry : m_sizeUrlHashMap.entrySet()) {
            if (!that.m_sizeUrlHashMap.containsKey(entry))
                return false;
            if (!that.m_sizeUrlHashMap.get(entry.getKey()).equals(entry.getValue()))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), m_sizeUrlHashMap);
    }
}
