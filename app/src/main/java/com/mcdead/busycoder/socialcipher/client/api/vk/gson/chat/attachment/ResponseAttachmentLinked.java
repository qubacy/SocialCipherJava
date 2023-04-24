package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;

import java.util.HashMap;

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

//    public ResponseAttachmentLinked(final String attachmentType,
//                                    final String attachmentId,
//                                    final String url)
//    {
//        super(attachmentType, attachmentId);
//
//        this.url = url;
//    }

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
}
