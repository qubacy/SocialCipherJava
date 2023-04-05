package com.mcdead.busycoder.socialcipher.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;

import java.net.URI;

public class AttachmentEntityGenerator {
    public static AttachmentEntityBase generateAttachmentByIdAndFilePath(
            final String fileId,
            final String filePath)
    {
        if (filePath == null) return null;
        if (filePath.isEmpty()) return null;

        String attachmentExtension = AttachmentContext.getExtensionByFilePath(filePath);

        if (attachmentExtension == null) return null;

        AttachmentType attachmentType = AttachmentType.getTypeByExtension(attachmentExtension);

        if (attachmentType == null) return null;

        URI uri = AttachmentContext.getURIByFilePath(filePath);

        if (uri == null) return null;

        return generateAttachmentByIdAndURI(attachmentType, fileId, uri);
    }

    private static AttachmentEntityBase generateAttachmentByIdAndURI(
            final AttachmentType attachmentType,
            final String fileId,
            final URI uri)
    {
        switch (attachmentType) {
            case IMAGE: return new AttachmentEntityImage(fileId, uri);
            case DOC: return new AttachmentEntityDoc(fileId, uri);
            case AUDIO: return new AttachmentEntityAudio(fileId, uri);
            case VIDEO: return new AttachmentEntityVideo(fileId, uri);
        }

        return null;
    }
}
