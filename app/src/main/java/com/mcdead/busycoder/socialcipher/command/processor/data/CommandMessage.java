package com.mcdead.busycoder.socialcipher.command.processor.data;

import java.io.Serializable;

public class CommandMessage implements Serializable {
    final private long m_peerId;
    final private long m_initializerPeerId;
    final private String m_commandString;

    private CommandMessage(
            final long peerId,
            final long initializerPeerId,
            final String commandString)
    {
        m_peerId = peerId;
        m_initializerPeerId = initializerPeerId;
        m_commandString = commandString;
    }

    public static CommandMessage getInstance(
            final long peerId,
            final long initializerPeerId,
            final String commandString)
    {
        if (peerId == 0 || initializerPeerId == 0) return null;
        if (commandString == null) return null;
        if (commandString.isEmpty()) return null;

        return new CommandMessage(peerId, initializerPeerId, commandString);
    }

    public long getPeerId() {
        return m_peerId;
    }

    public long getInitializerPeerId() {
        return m_initializerPeerId;
    }

    public String getCommandString() {
        return m_commandString;
    }
}
