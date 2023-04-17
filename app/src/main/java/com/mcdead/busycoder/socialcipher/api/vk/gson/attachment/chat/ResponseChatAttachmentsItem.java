package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.chat;

import com.google.gson.annotations.SerializedName;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;

public class ResponseChatAttachmentsItem {
    public @SerializedName("from_id") long fromId;
    public ResponseAttachmentBase attachment;
}
