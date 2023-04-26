package com.mcdead.busycoder.socialcipher.command.processor.data;

import java.io.Serializable;

public class CommandMessage implements Serializable {
    final private long m_peerId;
    final private String m_commandString;

    private CommandMessage(
            final long peerId,
            final String commandString)
    {
        m_peerId = peerId;
        m_commandString = commandString;
    }

    public static CommandMessage getInstance(
            final long peerId,
            final String commandString)
    {
        if (peerId == 0) return null;
        if (commandString == null) return null;
        if (commandString.isEmpty()) return null;

        return new CommandMessage(peerId, commandString);
    }

    public long getPeerId() {
        return m_peerId;
    }

    public String getCommandString() {
        return m_commandString;
    }
}
