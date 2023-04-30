package com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.processor.commandchecker;

import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;

public class CommandMessageRetriever {
    final private long m_chatId;
    final private long m_senderPeerId;
    final private long m_messageId;

    private CommandMessageRetriever(
            final long chatId,
            final long senderPeerId,
            final long messageId)
    {
        m_chatId = chatId;
        m_senderPeerId = senderPeerId;
        m_messageId = messageId;
    }

    public static CommandMessageRetriever getInstance(
            final long chatId,
            final long senderPeerId,
            final long messageId)
    {
        if (chatId == 0 || senderPeerId == 0)
            return null;

        return new CommandMessageRetriever(chatId, senderPeerId, messageId);
    }

    public CommandMessage retrieveCommandMessage(
            final String messageText)
    {
        return CommandMessage.getInstance(
                m_chatId,
                m_senderPeerId,
                m_messageId,
                messageText);
    }
}
