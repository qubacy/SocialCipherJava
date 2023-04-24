package com.mcdead.busycoder.socialcipher.client.data.entity.attachment;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AttachmentEntityGenerator {
    public static AttachmentEntityBase generateAttachmentByIdAndAttachmentSizeFilePathHashMap(
            final String fileId,
            final HashMap<AttachmentSize, String> attachmentSizeFilePathHashMap)
    {
        if (attachmentSizeFilePathHashMap == null) return null;
        if (attachmentSizeFilePathHashMap.isEmpty()) return null;
        if (!attachmentSizeFilePathHashMap.containsKey(AttachmentSize.STANDARD))
            return null;

        String standardSizeAttachmentFilePath =
                attachmentSizeFilePathHashMap.get(AttachmentSize.STANDARD);
        String attachmentExtension =
                AttachmentContext.getExtensionByFilePath(standardSizeAttachmentFilePath);

        if (attachmentExtension == null) return null;

        AttachmentType attachmentType = AttachmentType.getTypeByExtension(attachmentExtension);

        if (attachmentType == null) return null;

        HashMap<AttachmentSize, URI> attachmentSizeURIHashMap = new HashMap<>();

        for (final Map.Entry attachmentSizeFilePathItem : attachmentSizeFilePathHashMap.entrySet()) {
            URI uri = AttachmentContext.getURIByFilePath((String) attachmentSizeFilePathItem.getValue());

            if (uri == null) return null;

            attachmentSizeURIHashMap.put((AttachmentSize) attachmentSizeFilePathItem.getKey(), uri);
        }

        return generateAttachmentByIdAndURI(attachmentType, fileId, attachmentSizeURIHashMap);
    }

    private static AttachmentEntityBase generateAttachmentByIdAndURI(
            final AttachmentType attachmentType,
            final String fileId,
            final HashMap<AttachmentSize, URI> attachmentSizeURIHashMap)
    {
        switch (attachmentType) {
            case IMAGE: return new AttachmentEntityImage(fileId, attachmentSizeURIHashMap);
            case DOC: return new AttachmentEntityDoc(fileId, attachmentSizeURIHashMap);
            case AUDIO: return new AttachmentEntityAudio(fileId, attachmentSizeURIHashMap);
            case VIDEO: return new AttachmentEntityVideo(fileId, attachmentSizeURIHashMap);
        }

        return null;
    }
}
