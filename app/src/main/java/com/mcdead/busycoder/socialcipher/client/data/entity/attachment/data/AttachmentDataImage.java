package com.mcdead.busycoder.socialcipher.client.data.entity.attachment.data;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

public class AttachmentDataImage extends AttachmentData {
    protected AttachmentDataImage(
            final String name,
            final String extension,
            final byte[] bytes)
    {
        super(name, extension, bytes);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.IMAGE;
    }
}
