package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

public class ResponseAttachmentLinked extends ResponseAttachmentStored {
    public static final String C_URL_PROP_NAME = "url";

    public String url;

    public ResponseAttachmentLinked() {
        super();
    }

    @Override
    public ResponseAttachmentType getAttachmentType() {
        return ResponseAttachmentType.LINKED;
    }

    public ResponseAttachmentLinked(final String attachmentType,
                                    final String attachmentId,
                                    final String url)
    {
        super(attachmentType, attachmentId);

        this.url = url;
    }

    public ResponseAttachmentLinked(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId,
                                    final String url)
    {
        super(attachmentType, attachmentId, attachmentOwnerId);

        this.url = url;
    }
}
