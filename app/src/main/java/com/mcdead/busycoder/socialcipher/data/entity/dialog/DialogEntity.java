package com.mcdead.busycoder.socialcipher.data.entity.dialog;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class DialogEntity {
    private long m_dialogId = 0;
    private DialogType m_type = null;
    private volatile List<MessageEntity> m_messages = null;

    public DialogEntity(final long peerId,
                        final DialogType type)
    {
        m_dialogId = peerId;
        m_type = type;
        m_messages = new LinkedList<>();
    }

    public long getDialogId() {
        return m_dialogId;
    }

    public DialogType getType() {
        return m_type;
    }

    public MessageEntity getMessageByIndex(final int index) {
        if (index >= m_messages.size())
            return null;

        return m_messages.get(index);
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
            if (!m_messages.remove(messageId))
                return false;
        }

        return true;
    }

//    public boolean addMessage(final MessageEntity message) {
//        if (message == null) return false;
//
//        synchronized (m_messages) {
//            m_messages.add(message);
//        }
//
//        return true;
//    }
//
//    public boolean removeMessage(final long messageId) {
//        if (messageId == 0) return false;
//
//        synchronized (m_messages) {
//            m_messages.remove(messageId);
//        }
//
//        return true;
//    }
}
