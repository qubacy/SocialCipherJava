package com.mcdead.busycoder.socialcipher.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;

import java.net.URI;

public class AttachmentEntityDoc extends AttachmentEntityBase {
    public AttachmentEntityDoc(String id,
                               URI uri)
    {
        super(id, uri);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.DOC;
    }
}
