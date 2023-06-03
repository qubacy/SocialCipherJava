package com.mcdead.busycoder.socialcipher.client.data.entity.attachment.data;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

public class AttachmentDataGenerator {
    public static AttachmentData generateAttachmentData(
            final String name,
            final String extension,
            final byte[] bytes,
            final AttachmentType attachmentType)
    {
        if (name == null || extension == null || bytes == null || attachmentType == null)
            return null;
        if ((name.isEmpty() && extension.isEmpty()) || bytes.length <= 0)
            return null;

        switch (attachmentType) {
            case IMAGE: return new AttachmentDataImage(name, extension, bytes);
            case DOC: return new AttachmentDataDoc(name, extension, bytes);
        }

        return null;
    }
}
