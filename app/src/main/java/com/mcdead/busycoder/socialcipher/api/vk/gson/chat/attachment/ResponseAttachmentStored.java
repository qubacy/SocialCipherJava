package com.mcdead.busycoder.socialcipher.api.vk.gson.chat.attachment;

/*
*
* attachmentID - an unique identificator used in getting attachment requests.
*
*/
public class ResponseAttachmentStored extends ResponseAttachmentBase {
    protected long m_attachmentId = 0;
    protected long m_attachmentOwnerId = 0;
    protected String m_attachmentAccessKey = null;

    public ResponseAttachmentStored() {
        super();
    }

    public ResponseAttachmentStored(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId)
    {
        super(attachmentType);

        m_attachmentId = attachmentId;
        m_attachmentOwnerId = attachmentOwnerId;
        m_attachmentAccessKey = new String("");
    }

    public ResponseAttachmentStored(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId,
                                    final String attachmentAccessKey)
    {
        super(attachmentType);

        m_attachmentId = attachmentId;
        m_attachmentOwnerId = attachmentOwnerId;
        m_attachmentAccessKey = attachmentAccessKey;
    }

    protected ResponseAttachmentStored(
            final ResponseAttachmentStored basis)
    {
        super(basis.m_attachmentType);

        m_attachmentId = basis.m_attachmentId;
        m_attachmentOwnerId = basis.m_attachmentOwnerId;
        m_attachmentAccessKey = basis.m_attachmentAccessKey;
    }

    public static ResponseAttachmentStored generateResponseAttachmentFromFullAttachmentId(
            final String attachmentType,
            final String fullAttachmentId)
    {
        if (fullAttachmentId == null || attachmentType == null)
            return null;
        if (fullAttachmentId.isEmpty() || attachmentType.isEmpty())
            return null;

        String[] attachmentIdParts = fullAttachmentId.split("_");

        if (attachmentIdParts.length < 2) return null;

        long attachmentOwnerId = Long.parseLong(attachmentIdParts[0]);
        long attachmentId = Long.parseLong(attachmentIdParts[1]);

        ResponseAttachmentStored attachment =
                new ResponseAttachmentStored(attachmentType, attachmentId, attachmentOwnerId);

        if (attachmentIdParts.length == 3) {
            if (attachmentIdParts[2].isEmpty()) return null;

            attachment.m_attachmentAccessKey = attachmentIdParts[2];
        }

        return attachment;
    }

    @Override
    public ResponseAttachmentType getResponseAttachmentType() {
        return ResponseAttachmentType.STORED;
    }

    public long getAttachmentId() {
        return m_attachmentId;
    }

    public long getAttachmentOwnerId() {
        return m_attachmentOwnerId;
    }

    public String getAttachmentAccessKey() {
        return m_attachmentAccessKey;
    }

    public String getShortAttachmentId() {
        return (String.valueOf(m_attachmentOwnerId) + '_' + String.valueOf(m_attachmentId));
    }

    public String getFullAttachmentId() {
        return (
                String.valueOf(m_attachmentOwnerId) + '_' + String.valueOf(m_attachmentId) +
                        (m_attachmentAccessKey.isEmpty() ? "" : "_" + m_attachmentAccessKey));
    }

    public String getTypedShortAttachmentId() {
        return (m_attachmentType + getShortAttachmentId());
    }

    public String getTypedFullAttachmentID() {
        return m_attachmentType + getFullAttachmentId();
    }
}
