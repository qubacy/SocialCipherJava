package com.mcdead.busycoder.socialcipher.command.processor.executor;

import android.content.Context;
import android.os.Process;
import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandProcessor;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandProcessorCallback;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.parser.CommandDataParser;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/*

    Should be launched from the beginning;

*/
public class CommandExecutorAsync
        implements
        Runnable,
        CommandProcessorCallback
{
    private static final long C_TIMEOUT_MILLISECONDS = 300;

    final CipherCommandProcessor m_cipherProcessor;

    final private long m_localPeerId;
    final private Context m_context;
    final private LinkedBlockingQueue<CommandMessage> m_pendingCommandMessageQueueRef;

    private CommandExecutorAsync(
            final long localPeerId,
            final Context context,
            final LinkedBlockingQueue<CommandMessage> pendingCommandMessageQueueRef)
    {
        m_cipherProcessor = new CipherCommandProcessor();

        m_localPeerId = localPeerId;
        m_context = context;
        m_pendingCommandMessageQueueRef = pendingCommandMessageQueueRef;
    }

    public static CommandExecutorAsync getInstance(
            final long localPeerId,
            final Context context,
            final LinkedBlockingQueue<CommandMessage> pendingCommandMessageQueueRef)
    {
        if (context == null || localPeerId == 0
         || pendingCommandMessageQueueRef == null)
        {
            return null;
        }

        return new CommandExecutorAsync(
                localPeerId,
                context,
                pendingCommandMessageQueueRef);
    }

//    public boolean addCommandMessageToProcess(
//            final long peerId,
//            final MessageEntity message)
//    {
//        if (peerId == m_localPeerId) return true;
//
//        CommandMessage commandMessage = CommandMessage.getInstance(peerId, message);
//
//        if (commandMessage == null) return false;
//
//        m_pendingCommandMessageQueueRef.add(commandMessage);
//
//        return true;
//    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (!Thread.interrupted()) {
            SystemClock.sleep(C_TIMEOUT_MILLISECONDS);

            m_cipherProcessor.execState();

            CommandMessage commandMessage = m_pendingCommandMessageQueueRef.peek();

            if (commandMessage == null) continue;

            ObjectWrapper<CommandData> commandDataWrapper =
                    new ObjectWrapper<>();
            Error parsingDataError =
                    CommandDataParser.parseCommandMessage(commandMessage, commandDataWrapper);

            if (parsingDataError != null) {
                ErrorBroadcastReceiver.broadcastError(parsingDataError, m_context);

                continue;
            }

            Error conveyingCommandError = conveyCommandToProcessor(
                    commandDataWrapper.getValue());

            if (conveyingCommandError != null) {
                ErrorBroadcastReceiver.broadcastError(conveyingCommandError, m_context);

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

    @Override
    public void sendCommand(
            final long chatId,
            final List<Long> receiverPeerIdList,
            final String commandBody)
    {
        // todo: constructing a CommandData obj..

        // todo: serializing the obj..

        // todo: sending broadcast to SEND MESSAGE with the serialized data as text..


    }
}
