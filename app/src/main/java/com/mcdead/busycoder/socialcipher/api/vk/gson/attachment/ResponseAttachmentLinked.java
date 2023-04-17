package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

public class ResponseAttachmentLinked extends ResponseAttachmentStored {
    public static final String C_URL_PROP_NAME = "url";

    protected String m_url;

    public ResponseAttachmentLinked() {
        super();
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
                                    final String url)
    {
        super(attachmentType, attachmentId, attachmentOwnerId);

        m_url = url;
    }

    public ResponseAttachmentLinked(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId,
                                    final String attachmentAccessKey,
                                    final String url)
    {
        super(attachmentType, attachmentId, attachmentOwnerId, attachmentAccessKey);

        m_url = url;
    }

    protected ResponseAttachmentLinked(
            final ResponseAttachmentStored basis,
            final String url)
    {
        super(basis);

        m_url = url;
    }

    protected ResponseAttachmentLinked(
            final ResponseAttachmentLinked basis)
    {
        super(basis.m_attachmentType,
                basis.m_attachmentId,
                basis.m_attachmentOwnerId,
                basis.m_attachmentAccessKey);

        m_url = basis.m_url;
    }

    public static ResponseAttachmentLinked generateAttachmentLinkedWithFullAttachmentId(
            final String attachmentType,
            final String fullAttachmentId,
            final String url)
    {
        if (url == null) return null;
        if (url.isEmpty()) return null;

        ResponseAttachmentStored attachmentBasis =
                ResponseAttachmentStored.generateResponseAttachmentFromFullAttachmentId(
                        attachmentType, fullAttachmentId);

        if (attachmentBasis == null) return null;

        ResponseAttachmentLinked attachmentLinked =
                new ResponseAttachmentLinked(attachmentBasis, url);

        return attachmentLinked;
    }

    public String getUrl() {
        return m_url;
    }
}
