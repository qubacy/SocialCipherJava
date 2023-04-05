package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

public class ResponseAttachmentDoc extends ResponseAttachmentLinked {
    public static final String C_EXT_PROP_NAME = "ext";

    public String ext;

    public ResponseAttachmentDoc(final String attachmentType,
                                 final String attachmentId,
                                 final String url,
                                 final String ext)
    {
        super(attachmentType, attachmentId, url);

        this.ext = ext;
    }

    public ResponseAttachmentDoc(final String attachmentType,
                                 final long attachmentId,
                                 final long attachmentOwnerId,
                                 final String url,
                                 final String ext)
    {
        super(attachmentType, attachmentId, attachmentOwnerId, url);

        this.ext = ext;
    }
}
