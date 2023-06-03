package com.mcdead.busycoder.socialcipher.client.data.entity.chat;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class ChatEntity {
    final private long m_id;
    final private ChatType m_type;
    private volatile List<MessageEntity> m_messages = null;

    protected ChatEntity(final long peerId,
                      final ChatType type)
    {
        m_id = peerId;
        m_type = type;
        m_messages = new LinkedList<>();
    }

    public long getId() {
        return m_id;
    }

    public ChatType getType() {
        return m_type;
    }

    public MessageEntity getMessageByIndex(final int index) {
        synchronized (m_messages) {
            if (index >= m_messages.size())
                return null;

            return m_messages.get(index);
        }
    }

    public MessageEntity getMessageById(final long id) {
        synchronized (m_messages) {
            for (final MessageEntity message : m_messages) {
                if (message.getId() == id) return message;
            }
        }

        return null;
    }

    public MessageEntity getLastMessage() {
        if (m_messages.isEmpty()) return null;

        synchronized (m_messages) {
            return m_messages.get(m_messages.size() - 1);
        }
    }

    public List<MessageEntity> getMessages() {
        synchronized (m_messages) {
            return new ArrayList<>(m_messages);
        }
    }

    public boolean addMessage(final MessageEntity message) {
        if (message == null) return false;

        synchronized (m_messages) {
            if (!m_messages.add(message))
                return false;
        }

        return true;
    }

    public boolean removeMessageById(final long messageId) {
        if (messageId == 0) return false;

        synchronized (m_messages) {
            for (final MessageEntity message : m_messages) {
                if (message.getId() == messageId)
                    return m_messages.remove(message);
            }
        }

        return false;
    }

    public boolean areAttachmentsLoaded() {
        for (final MessageEntity message : m_messages) {
            if (!message.areAttachmentsLoaded())
                return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChatEntity that = (ChatEntity) o;

        return m_id == that.m_id &&
                m_type == that.m_type &&
                Objects.equals(m_messages, that.m_messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_id, m_type, m_messages);
    }
}
