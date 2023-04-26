package com.mcdead.busycoder.socialcipher.command.processor.data;

import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;

public class CommandMessage {
    final private long m_peerId;
    final private MessageEntity m_messageEntity;

    private CommandMessage(
            final long peerId,
            final MessageEntity messageEntity)
    {
        m_peerId = peerId;
        m_messageEntity = messageEntity;
    }

    public static CommandMessage getInstance(
            final long peerId,
            final MessageEntity messageEntity)
    {
        if (peerId == 0) return null;
        if (messageEntity == null) return null;
        if (messageEntity.getMessage() == null) return null;
        if (messageEntity.getMessage().isEmpty()) return null;

        return new CommandMessage(peerId, messageEntity);
    }

    public long getPeerId() {
        return m_peerId;
    }

    public MessageEntity getMessageEntity() {
        return m_messageEntity;
    }
}
