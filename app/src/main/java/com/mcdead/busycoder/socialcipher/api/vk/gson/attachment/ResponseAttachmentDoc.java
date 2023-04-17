package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

public class ResponseAttachmentDoc extends ResponseAttachmentLinked {
    public static final String C_EXT_PROP_NAME = "ext";

    private String m_ext = null;

//    public ResponseAttachmentDoc(final String attachmentType,
//                                 final String attachmentId,
//                                 final String url,
//                                 final String ext)
//    {
//        super(attachmentType, attachmentId, url);
//
//        this.ext = ext;
//    }

    public ResponseAttachmentDoc(final String attachmentType,
                                 final long attachmentId,
                                 final long attachmentOwnerId,
                                 final String url,
                                 final String ext)
    {
        super(attachmentType, attachmentId, attachmentOwnerId, url);

        this.m_ext = ext;
    }

    protected ResponseAttachmentDoc(
            final ResponseAttachmentLinked basis,
            final String ext)
    {
        super(basis);

        m_ext = ext;
    }

    public static ResponseAttachmentDoc generateAttachmentDocWithFullAttachmentId(
            final String attachmentType,
            final String fullAttachmentId,
            final String url,
            final String ext)
    {
        if (ext == null) return null;

        ResponseAttachmentLinked attachmentBasis =
                ResponseAttachmentLinked.generateAttachmentLinkedWithFullAttachmentId(
                        attachmentType, fullAttachmentId, url);

        if (attachmentBasis == null) return null;

        ResponseAttachmentDoc attachmentDoc =
                new ResponseAttachmentDoc(attachmentBasis, ext);

        return attachmentDoc;
    }

    public String getExtension() {
        return m_ext;
    }
}
