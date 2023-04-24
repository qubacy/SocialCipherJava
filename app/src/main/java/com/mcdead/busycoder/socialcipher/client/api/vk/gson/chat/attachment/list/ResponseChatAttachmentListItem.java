package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.list;

import com.google.gson.annotations.SerializedName;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;

public class ResponseChatAttachmentListItem {
    public @SerializedName("from_id") long fromId;
    public ResponseAttachmentBase attachment;
}
