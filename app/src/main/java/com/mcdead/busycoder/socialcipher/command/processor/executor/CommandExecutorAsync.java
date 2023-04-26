package com.mcdead.busycoder.socialcipher.command.processor.executor;

import android.os.Process;
import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandProcessor;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.parser.CommandDataParser;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/*

    Should be launched from the beginning;

*/
public class CommandExecutorAsync implements Runnable {
    private static CommandExecutorAsync s_instance = null;

    final CipherCommandProcessor m_cipherProcessor;

    final private Queue<CommandMessage> m_commandMessageQueue;

    private CommandExecutorAsync() {
        m_cipherProcessor = new CipherCommandProcessor();

        m_commandMessageQueue = new LinkedBlockingQueue<>();
    }

    public static CommandExecutorAsync getInstance() {
        if (s_instance == null)
            s_instance = new CommandExecutorAsync();

        return s_instance;
    }

    public boolean addCommandMessageToProcess(
            final long peerId,
            final MessageEntity message)
    {
        CommandMessage commandMessage = CommandMessage.getInstance(peerId, message);

        if (commandMessage == null) return false;

        m_commandMessageQueue.add(commandMessage);

        return true;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (!Thread.interrupted()) {
            CommandMessage commandMessage = m_commandMessageQueue.peek();

            if (commandMessage == null) continue;

            ObjectWrapper<CommandData> commandDataWrapper =
                    new ObjectWrapper<>();
            Error parsingDataError =
                    CommandDataParser.parseCommandMessage(commandMessage, commandDataWrapper);

            if (parsingDataError != null) {
                // todo: error processing..

                continue;
            }

            Error conveyingCommandError = conveyCommandToProcessor(
                    commandDataWrapper.getValue());

            if (conveyingCommandError != null) {
                // todo: error processing..

                continue;
            }
        }
    }

    private Error conveyCommandToProcessor(
            final CommandData commandData)
    {
        switch (commandData.getCategory()) {
            case CIPHER: return m_cipherProcessor.processCommand(commandData);
        }

        return new Error("Unknown command category!", true);
    }
}
