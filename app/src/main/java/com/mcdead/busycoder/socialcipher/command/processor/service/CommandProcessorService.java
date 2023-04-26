package com.mcdead.busycoder.socialcipher.command.processor.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.executor.CommandExecutorAsync;

import java.util.concurrent.LinkedBlockingQueue;

public class CommandProcessorService extends Service {
    public static final String C_LOCAL_USER_ID_PROP_NAME = "localPeerId";

    public static final String C_OPERATION_ID_PROP_NAME = "operationID";
    public static final String C_COMMAND_MESSAGE_PROP_NAME = "commandMessage";

    private Thread m_cipherProcessorThread = null;

    private long m_localPeerId = 0;

    private volatile LinkedBlockingQueue<CommandMessage> m_pendingCommandMessageQueue;

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
            case INIT_SERVICE: operationError = processInit(intent); break;
            case PROCESS_COMMAND_MESSAGE: operationError = processCommandMessage(intent); break;
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


    }

    @Override
    public void onDestroy() {
        if (m_cipherProcessorThread != null)
            if (m_cipherProcessorThread.isAlive())
                m_cipherProcessorThread.interrupt();

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
        if (m_cipherProcessorThread == null) {
            m_cipherProcessorThread =
                    new Thread(CommandExecutorAsync.getInstance(
                            m_localPeerId,
                            getApplicationContext(),
                            m_pendingCommandMessageQueue));

            m_cipherProcessorThread.start();
        }

        CommandMessage commandMessage =
                (CommandMessage) intent.getSerializableExtra(C_COMMAND_MESSAGE_PROP_NAME);

        if (commandMessage == null)
            return new Error("Provided Command Message was null!", true);

        m_pendingCommandMessageQueue.offer(commandMessage);

        return null;
    }

    public enum OperationType {
        INIT_SERVICE(1),
        PROCESS_COMMAND_MESSAGE(2);

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
