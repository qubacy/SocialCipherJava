package com.mcdead.busycoder.socialcipher.client.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

import java.net.URI;
import java.util.HashMap;

public class AttachmentEntityDoc extends AttachmentEntityBase {
    protected AttachmentEntityDoc(
            final String id,
            final HashMap<AttachmentSize, URI> sizeUriHashMap)
    {
        super(id, sizeUriHashMap);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.DOC;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
