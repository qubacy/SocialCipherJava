package com.mcdead.busycoder.socialcipher.command.processor.executor;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandProcessor;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandProcessorCallback;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.processor.CommandProcessorCallback;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.preparer.parser.CommandDataParser;
import com.mcdead.busycoder.socialcipher.command.processor.preparer.serializer.CommandDataSerializer;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/*

    Should be launched from the beginning;

*/
public class CommandExecutorAsync
        implements
        Runnable,
        CommandProcessorCallback,
        CipherCommandProcessorCallback
{
    private static final long C_TIMEOUT_MILLISECONDS = 300;
    private static final long C_REQUEST_ANSWER_TIMEOUT = 3000;

    final CipherCommandProcessor m_cipherProcessor;

    final private long m_localPeerId;
    final private Context m_context;
    final private LinkedBlockingQueue<CommandMessage> m_pendingCommandMessageQueueRef;

    private CommandMessage m_currentCommandMessage = null;
    private volatile RequestAnswer m_currentRequestAnswer = null;

    private CommandExecutorAsync(
            final long localPeerId,
            final Context context,
            final LinkedBlockingQueue<CommandMessage> pendingCommandMessageQueueRef)
    {
        m_cipherProcessor = new CipherCommandProcessor(this);

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

    public synchronized void processRequestAnswer(
            final RequestAnswer requestAnswer)
    {
        // todo: processing the request answer..

        if (m_currentRequestAnswer == null)
            m_currentRequestAnswer = requestAnswer;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (!Thread.interrupted()) {
            SystemClock.sleep(C_TIMEOUT_MILLISECONDS);

            Error execError = m_cipherProcessor.execState();

            if (execError != null) {
                ErrorBroadcastReceiver.broadcastError(execError, m_context);

                continue;
            }

            CommandMessage commandMessage = m_pendingCommandMessageQueueRef.peek();

            if (commandMessage == null) continue;

            m_currentCommandMessage = commandMessage;

            ObjectWrapper<CommandData> commandDataWrapper =
                    new ObjectWrapper<>();
            Error parsingDataError =
                    CommandDataParser.parseCommandMessage(commandMessage, commandDataWrapper);

            if (parsingDataError != null) {
                ErrorBroadcastReceiver.broadcastError(parsingDataError, m_context);

                continue;
            }

            Error conveyingCommandError = conveyCommandToProcessor(
                    commandDataWrapper.getValue(), commandMessage.getInitializerPeerId());

            if (conveyingCommandError != null) {
                ErrorBroadcastReceiver.broadcastError(conveyingCommandError, m_context);

                continue;
            }
        }
    }

    private Error conveyCommandToProcessor(
            final CommandData commandData,
            final long initializerPeerId)
    {
        switch (commandData.getCategory()) {
            case CIPHER: return m_cipherProcessor.processCommand(commandData, initializerPeerId);
        }

        return new Error("Unknown command category!", true);
    }

    @Override
    public void sendCommand(
            final CommandCategory commandCategory,
            final long chatId,
            final List<Long> receiverPeerIdList,
            final String commandBody)
    {
        CommandData commandData =
                new CommandData(
                        commandCategory,
                        chatId,
                        receiverPeerIdList,
                        commandBody);

        ObjectWrapper<String> serializedCommandDataWrapper = new ObjectWrapper<>();
        Error serializationError =
                CommandDataSerializer.serializeCommandData(
                        commandData,
                        serializedCommandDataWrapper);

        if (serializationError != null) {
            ErrorBroadcastReceiver.broadcastError(serializationError, m_context);

            return;
        }

        Intent intent = new Intent(ChatListBroadcastReceiver.C_SEND_COMMAND_MESSAGE);

        intent.putExtra(
                ChatListBroadcastReceiver.C_SEND_COMMAND_CHAT_ID_PROP_NAME,
                chatId);
        intent.putExtra(
                ChatListBroadcastReceiver.C_SEND_COMMAND_TEXT_PROP_NAME,
                serializedCommandDataWrapper.getValue());

        LocalBroadcastManager.
                getInstance(m_context).
                sendBroadcast(intent);
    }

    @Override
    public CipherRequestAnswerSettingSession onCipherSessionSettingRequestReceived() {
        Intent intent = new Intent(ChatBroadcastReceiver.C_SETTING_CIPHER_SESSION_ANSWER_REQUESTED);

        intent.putExtra(
                ChatBroadcastReceiver.C_CHAT_ID_PROP_NAME,
                m_currentCommandMessage.getPeerId());
        intent.putExtra(
                ChatBroadcastReceiver.C_INITIALIZER_PEER_ID_PROP_NAME,
                m_currentCommandMessage.getInitializerPeerId());

        LocalBroadcastManager.
                getInstance(m_context).
                sendBroadcast(intent);

        long endTime = System.currentTimeMillis() + C_REQUEST_ANSWER_TIMEOUT;

        while (endTime > System.currentTimeMillis()) {
            SystemClock.sleep(C_TIMEOUT_MILLISECONDS);

            if (m_currentRequestAnswer == null) continue;

            CipherRequestAnswerSettingSession settingCipherSessionRequestAnswer =
                    (CipherRequestAnswerSettingSession) m_currentRequestAnswer;

            m_currentRequestAnswer = null;

            return settingCipherSessionRequestAnswer;
        }

        return null;
    }

    @Override
    public long getLocalPeerId() {
        return m_localPeerId;
    }
}
