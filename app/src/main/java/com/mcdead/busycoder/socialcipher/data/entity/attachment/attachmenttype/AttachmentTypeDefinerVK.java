package com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype;

public class AttachmentTypeDefinerVK implements AttachmentTypeDefinerInterface {
    public static final String C_IMAGE_TYPE_NAME = "photo";
    public static final String C_DOC_TYPE_NAME = "doc";
    public static final String C_VIDEO_TYPE_NAME = "video";

    @Override
    public AttachmentType defineAttachmentTypeByString(String type) {
        if (type == null) return null;

        switch (type) {
            case C_IMAGE_TYPE_NAME: return AttachmentType.IMAGE;
            case C_DOC_TYPE_NAME: return AttachmentType.DOC;
            case C_VIDEO_TYPE_NAME: return AttachmentType.VIDEO;
        }

        return null;
    }
}
