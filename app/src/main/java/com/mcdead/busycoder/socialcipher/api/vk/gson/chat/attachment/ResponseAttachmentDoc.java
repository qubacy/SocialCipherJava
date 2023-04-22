package com.mcdead.busycoder.socialcipher.api.vk.gson.chat.attachment;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.size.AttachmentSize;

import java.util.HashMap;

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
                                 final HashMap<AttachmentSize, String> sizeUrlHashMap,
                                 final String ext)
    {
        super(attachmentType, attachmentId, attachmentOwnerId, sizeUrlHashMap);

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
            HashMap<AttachmentSize, String> sizeUrlHashMap,
            final String ext)
    {
        if (ext == null) return null;

        ResponseAttachmentLinked attachmentBasis =
                ResponseAttachmentLinked.generateAttachmentLinkedWithFullAttachmentId(
                        attachmentType, fullAttachmentId, sizeUrlHashMap);

        if (attachmentBasis == null) return null;

        ResponseAttachmentDoc attachmentDoc =
                new ResponseAttachmentDoc(attachmentBasis, ext);

        return attachmentDoc;
    }

    public String getExtension() {
        return m_ext;
    }
}
