package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialog.ResponseAttachmentInterface;

import java.io.Serializable;

public abstract class ResponseAttachmentBase implements Serializable, ResponseAttachmentInterface {
    public String attachmentType;

    public ResponseAttachmentBase() {

    }

    public ResponseAttachmentBase(final String attachmentType)
    {
        this.attachmentType = attachmentType;
    }

    public abstract ResponseAttachmentType getAttachmentType();
}
