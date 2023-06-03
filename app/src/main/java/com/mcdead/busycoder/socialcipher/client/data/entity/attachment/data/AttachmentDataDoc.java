package com.mcdead.busycoder.socialcipher.client.data.entity.attachment.data;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

public class AttachmentDataDoc extends AttachmentData{

    protected AttachmentDataDoc(
            final String name,
            final String extension,
            byte[] bytes)
    {
        super(name, extension, bytes);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.DOC;
    }
}
