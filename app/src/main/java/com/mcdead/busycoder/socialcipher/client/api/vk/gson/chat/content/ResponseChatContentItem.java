package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content;

import com.google.gson.annotations.SerializedName;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseMessageInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;

import java.util.List;
import java.util.Objects;

public class ResponseChatContentItem implements ResponseMessageInterface {
    public static final String C_ID_PROP_NAME = "id";
    public static final String C_FROM_ID_PROP_NAME = "from_id";
    public static final String C_PEER_ID_PROP_NAME = "peer_id";
    public static final String C_TIMESTAMP_PROP_NAME = "date";
    public static final String C_TEXT_PROP_NAME = "text";
    public static final String C_ATTACHMENTS_PROP_NAME = "attachments";

    public long id = 0;
    public @SerializedName(C_FROM_ID_PROP_NAME) long fromId = 0;
    public @SerializedName(C_PEER_ID_PROP_NAME) long peerId = 0;
    public @SerializedName(C_TIMESTAMP_PROP_NAME) long timestamp = 0;
    public String text = null;

    public List<ResponseAttachmentBase> attachments = null;

    public ResponseChatContentItem() {

    }

    public ResponseChatContentItem(
            final long id,
            final long fromId,
            final long peerId,
            final long timestamp,
            final String text,
            final List<ResponseAttachmentBase> attachments)
    {
        this.id = id;
        this.fromId = fromId;
        this.peerId = peerId;
        this.timestamp = timestamp;
        this.text = text;
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResponseChatContentItem that = (ResponseChatContentItem) o;

        return id == that.id &&
                fromId == that.fromId &&
                peerId == that.peerId &&
                timestamp == that.timestamp &&
                Objects.equals(text, that.text) &&
                Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromId, peerId, timestamp, text, attachments);
    }
}
