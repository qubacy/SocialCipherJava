package com.mcdead.busycoder.socialcipher.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.type.AttachmentType;

import java.net.URI;
import java.util.HashMap;

public class AttachmentEntityAudio extends AttachmentEntityBase {

    public AttachmentEntityAudio(final String id,
                                 final HashMap<AttachmentSize, URI> sizeUriHashMap)
    {
        super(id, sizeUriHashMap);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.AUDIO;
    }
}
