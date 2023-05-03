package com.mcdead.busycoder.socialcipher.client.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

import java.net.URI;
import java.util.HashMap;

public class AttachmentEntityImage extends AttachmentEntityBase {
    public AttachmentEntityImage(final String id,
                                 final HashMap<AttachmentSize, URI> sizeUriHashMap)
    {
        super(id, sizeUriHashMap);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.IMAGE;
    }
}