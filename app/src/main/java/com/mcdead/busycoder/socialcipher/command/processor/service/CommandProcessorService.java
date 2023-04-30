package com.mcdead.busycoder.socialcipher.command.processor.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.executor.CommandExecutorAsync;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapperSynchronized;

import java.util.concurrent.LinkedBlockingQueue;

public class CommandProcessorService extends Service
    implements CommandProcessorServiceBroadcastReceiverCallback
{
    public static final String C_LOCAL_USER_ID_PROP_NAME = "localPeerId";

    public static final String C_OPERATION_ID_PROP_NAME = "operationID";

    private CommandExecutorAsync m_commandExecutorRef = null;
    private Thread m_commandExecutorThread = null;

    private long m_localPeerId = 0;

    final private ObjectWrapperSynchronized<RequestAnswer> m_currentRequestAnswerShared;
    final private ObjectWrapperSynchronized<Long> m_currentNewSessionChatIdShared;
    final private LinkedBlockingQueue<CommandMessage> m_pendingCommandMessageQueueShared;

    private CommandProcessorServiceBroadcastReceiver m_broadcastReceiver = null;

    public CommandProcessorService() {
        m_currentRequestAnswerShared = new ObjectWrapperSynchronized<>();
        m_currentNewSessionChatIdShared = new ObjectWrapperSynchronized<>();
        m_pendingCommandMessageQueueShared = new LinkedBlockingQueue<>();
    }

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId)
    {
        if (intent == null) return START_NOT_STICKY;

        int operationID = intent.getIntExtra(C_OPERATION_ID_PROP_NAME, -1);
        OperationType operationType = OperationType.getOperationTypeById(operationID);

        if (operationType == null) return START_NOT_STICKY;

        Error operationError = null;

        switch (operationType) {
            case INIT_SERVICE:
                operationError = processInit(intent); break;
        }

        if (operationError != null) {
            ErrorBroadcastReceiver.broadcastError(operationError, this);

            return START_NOT_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter =
                new IntentFilter(CommandProcessorServiceBroadcastReceiver.C_PROVIDE_REQUEST_ANSWER);

        intentFilter.addAction(CommandProcessorServiceBroadcastReceiver.C_PROCESS_COMMAND_MESSAGE);
        intentFilter.addAction(CommandProcessorServiceBroadcastReceiver.C_INITIALIZE_NEW_CIPHERING_SESSION);

        m_broadcastReceiver = new CommandProcessorServiceBroadcastReceiver(this);

        LocalBroadcastManager.
                getInstance(getApplicationContext()).
                registerReceiver(m_broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (m_commandExecutorThread != null) {
            if (m_commandExecutorThread.isAlive())
                m_commandExecutorThread.interrupt();
        }

        LocalBroadcastManager.
                getInstance(getApplicationContext()).
                unregisterReceiver(m_broadcastReceiver);

        Log.d(getClass().getName(), "onDestroy() is on operating!");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Error processInit(
            final Intent intent)
    {
        long localPeerId = intent.getLongExtra(C_LOCAL_USER_ID_PROP_NAME, 0);

        if (localPeerId == 0)
            return new Error("Provided Local User Id was 0!", true);

        m_localPeerId = localPeerId;

        return null;
    }

    private void launchCommandExecutors() {
        if (m_commandExecutorThread != null)
            if (!m_commandExecutorThread.isInterrupted())
                return;

        // todo: m_commandExecutorRef will get nullified!

        m_commandExecutorRef =
                CommandExecutorAsync.getInstance(
                        m_localPeerId,
                        getApplicationContext(),
                        m_pendingCommandMessageQueueShared,
                        m_currentRequestAnswerShared,
                        m_currentNewSessionChatIdShared);
        m_commandExecutorThread = new Thread(m_commandExecutorRef);

        m_commandExecutorThread.start();
    }

    @Override
    public void onCommandMessageReceived(
            final CommandMessage commandMessage)
    {
        launchCommandExecutors();

        m_pendingCommandMessageQueueShared.offer(commandMessage);
    }

    @Override
    public void onRequestAnswered(
            final RequestAnswer requestAnswer)
    {
        if (m_commandExecutorThread == null || m_commandExecutorRef == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Command Processor hasn't been started yet!", true),
                    getApplicationContext());

            return;
        }

        //m_commandExecutorRef.processRequestAnswer(requestAnswer);
        m_currentRequestAnswerShared.setValue(requestAnswer);
    }

    @Override
    public void onNewSessionInitializingRequested(
            final long chatId)
    {
        launchCommandExecutors();

        //m_commandExecutorRef.initializeNewCipherSession(chatId);
        m_currentNewSessionChatIdShared.setValue(chatId);
    }

    @Override
    public void onServiceBroadcastReceiverErrorOccurred(
            final Error error)
    {
        if (error == null) return;

        ErrorBroadcastReceiver.broadcastError(error, this);
    }

    public enum OperationType {
        INIT_SERVICE(1);

        private int m_id = 0;

        private OperationType(final int id) {
            m_id = id;
        }

        public int getId() {
            return m_id;
        }

        public static OperationType getOperationTypeById(final int id) {
            for (final OperationType operationType : OperationType.values()) {
                if (operationType.m_id == id)
                    return operationType;
            }

            return null;
        }
    }
}
