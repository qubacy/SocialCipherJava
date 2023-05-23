package com.mcdead.busycoder.socialcipher.client.api.vk.gson.update;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ResponseUpdateItem implements ResponseUpdateItemInterface, Serializable {
    public int eventType = 0;
    public long messageId = 0;
    public int flags = 0;
    public long chatId = 0;
    public long timestamp = 0;
    public String text = null;
    public long fromPeerId = 0;
    public List<ResponseAttachmentBase> attachments = null;

    public ResponseUpdateItem() {

    }

    public ResponseUpdateItem(
            final int eventType,
            final long messageId,
            final int flags,
            final long chatId,
            final long timestamp,
            final String text,
            final long fromPeerId,
            final List<ResponseAttachmentBase> attachments)
    {
        this.eventType = eventType;
        this.messageId = messageId;
        this.flags = flags;
        this.chatId = chatId;
        this.timestamp = timestamp;
        this.text = text;
        this.fromPeerId = fromPeerId;
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResponseUpdateItem that = (ResponseUpdateItem) o;

        return eventType == that.eventType &&
                messageId == that.messageId &&
                flags == that.flags &&
                chatId == that.chatId &&
                timestamp == that.timestamp &&
                fromPeerId == that.fromPeerId &&
                Objects.equals(text, that.text) &&
                Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                eventType, messageId, flags, chatId, timestamp, text, fromPeerId, attachments);
    }
}
