package com.mcdead.busycoder.socialcipher.command.processor.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandProcessor;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.executor.CommandExecutorAsync;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;

import java.util.concurrent.LinkedBlockingQueue;

public class CommandProcessorService extends Service
    implements CommandProcessorServiceBroadcastReceiverCallback
{
    public static final String C_LOCAL_USER_ID_PROP_NAME = "localPeerId";

    public static final String C_OPERATION_ID_PROP_NAME = "operationID";
    public static final String C_COMMAND_MESSAGE_PROP_NAME = "commandMessage";

    public static final String C_REQUEST_ANSWER_PROP_NAME = "requestAnswer";

    private CommandExecutorAsync m_commandExecutorRef = null;
    private Thread m_commandExecutorThread = null;

    private long m_localPeerId = 0;

    private volatile LinkedBlockingQueue<CommandMessage> m_pendingCommandMessageQueue;

    private CommandProcessorServiceBroadcastReceiver m_broadcastReceiver = null;

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

        m_broadcastReceiver = new CommandProcessorServiceBroadcastReceiver(this);

        registerReceiver(m_broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (m_commandExecutorThread != null) {
            if (m_commandExecutorThread.isAlive())
                m_commandExecutorThread.interrupt();
        }

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

    private Error processCommandMessage(
            final Intent intent)
    {
        if (m_commandExecutorThread == null) {
            m_commandExecutorRef =
                    CommandExecutorAsync.getInstance(
                        m_localPeerId,
                        getApplicationContext(),
                        m_pendingCommandMessageQueue);
            m_commandExecutorThread = new Thread(m_commandExecutorRef);

            m_commandExecutorThread.start();
        }

        CommandMessage commandMessage =
                (CommandMessage) intent.getSerializableExtra(C_COMMAND_MESSAGE_PROP_NAME);

        if (commandMessage == null)
            return new Error("Provided Command Message was null!", true);

        m_pendingCommandMessageQueue.offer(commandMessage);

        return null;
    }

    private Error processRequestAnswered(
            final Intent intent)
    {
        if (m_commandExecutorThread == null || m_commandExecutorRef == null)
            return new Error("Command Processor hasn't been started yet!", true);

        RequestAnswer requestAnswer =
                (RequestAnswer) intent.getSerializableExtra(C_REQUEST_ANSWER_PROP_NAME);

        if (requestAnswer == null)
            return new Error("Bad Request Answer has been provided!", true);

        m_commandExecutorRef.processRequestAnswer(requestAnswer);

        return null;
    }

    @Override
    public void onDataReceived(
            final Intent data)
    {
        int operationID = data.getIntExtra(C_OPERATION_ID_PROP_NAME, -1);
        OperationType operationType = OperationType.getOperationTypeById(operationID);

        if (operationType == null) return;

        Error operationError = null;

        switch (operationType) {
            case PROCESS_COMMAND_MESSAGE:
                operationError = processCommandMessage(data); break;
            case PROCESS_REQUEST_ANSWERED:
                operationError = processRequestAnswered(data); break;
        }

        if (operationError != null)
            ErrorBroadcastReceiver.broadcastError(operationError, this);
    }

    @Override
    public void onServiceBroadcastReceiverErrorOccurred(
            final Error error)
    {
        if (error == null) return;

        ErrorBroadcastReceiver.broadcastError(error, this);
    }

    public enum OperationType {
        INIT_SERVICE(1),

        PROCESS_COMMAND_MESSAGE(2),
        PROCESS_REQUEST_ANSWERED(3);

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
