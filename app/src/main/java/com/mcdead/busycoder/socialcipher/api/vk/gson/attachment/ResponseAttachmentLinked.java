package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

public class ResponseAttachmentLinked extends ResponseAttachmentBase {
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
                                    final String url)
    {
        super(attachmentType);

        this.url = url;
    }
}
