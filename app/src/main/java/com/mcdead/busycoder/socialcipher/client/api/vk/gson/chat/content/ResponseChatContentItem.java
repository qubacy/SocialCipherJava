package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content;

import com.google.gson.annotations.SerializedName;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;

import java.util.List;

public class ResponseChatContentItem implements ResponseMessageInterface {
    public static final String C_ID_PROP_NAME = "id";
    public static final String C_FROM_ID_PROP_NAME = "from_id";
    public static final String C_PEER_ID_PROP_NAME = "peer_id";
    public static final String C_TIMESTAMP_PROP_NAME = "date";
    public static final String C_TEXT_PROP_NAME = "text";
    public static final String C_ATTACHMENTS_PROP_NAME = "attachments";

    public long id;
    public @SerializedName(C_FROM_ID_PROP_NAME) long fromId;
    public @SerializedName(C_PEER_ID_PROP_NAME) long peerId;
    public @SerializedName(C_TIMESTAMP_PROP_NAME) long timestamp;
    public String text;

    public List<ResponseAttachmentBase> attachments;
}
