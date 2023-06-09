package com.mcdead.busycoder.socialcipher.client.processor.update.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.processor.update.checker.UpdateCheckerAsyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.network.update.processor.UpdateProcessorAsyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.update.checker.UpdateCheckerAsyncBase;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class UpdateProcessorService extends Service {
    public static final String C_OPERATION_ID_PROP_NAME = "operationID";

    private Thread m_updateCheckerThread = null;
    private Thread m_messageProcessorThread = null;

    private UpdateCheckerAsyncBase m_updateChecker = null;

    private volatile LinkedBlockingQueue<ResponseUpdateItemInterface> m_pendingUpdates = null;

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId)
    {
        if (intent == null) return START_NOT_STICKY;

        int operationID = intent.getIntExtra(C_OPERATION_ID_PROP_NAME, -1);
        OperationType operationType = OperationType.getOperationTypeById(operationID);

        if (operationType == null) return START_NOT_STICKY;

        boolean result = false;

        switch (operationType) {
            case START_UPDATE_CHECKER: result = processStartUpdateChecker(); break;
            case PROCESS_RECEIVED_UPDATES: result = processReceivedUpdates(intent); break;
        }

        if (!result) return START_NOT_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (m_updateCheckerThread != null) {
            if (m_updateCheckerThread.isAlive()) {
                m_updateCheckerThread.interrupt();

                if (m_updateChecker != null)
                    m_updateChecker.interruptChecking();
            }
        }
        if (m_messageProcessorThread != null)
            if (m_messageProcessorThread.isAlive())
                m_messageProcessorThread.interrupt();

        Log.d(getClass().getName(), "onDestroy() is on operating!");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean processStartUpdateChecker() {
        if (m_updateCheckerThread != null) return false;

        Log.d("TEST", "Update checker thread creation..");

        UpdateCheckerAsyncBase updateChecker =
                UpdateCheckerAsyncFactory.generateUpdateChecker(this);

        if (updateChecker == null) return false;

        m_updateChecker = updateChecker;
        m_updateCheckerThread = new Thread(m_updateChecker);

        m_updateCheckerThread.start();

        return true;
    }

    private boolean processReceivedUpdates(Intent data) {
        if (data == null) return false;

        if (m_messageProcessorThread == null) {
            Log.d("TEST", "Message processor thread creation..");

            m_pendingUpdates = new LinkedBlockingQueue<ResponseUpdateItemInterface>();

            m_messageProcessorThread = new Thread(
                UpdateProcessorAsyncFactory.generateUpdateProcessor(getApplicationContext(), m_pendingUpdates)
            );

            m_messageProcessorThread.start();
        }

        Bundle updatesWrapperBundle =
                data.getBundleExtra(ChatListBroadcastReceiver.C_UPDATES_WRAPPER_EXTRA_PROP_NAME);
        List<ResponseUpdateItemInterface> updates =
                (List<ResponseUpdateItemInterface>) updatesWrapperBundle.
                        getSerializable(ChatListBroadcastReceiver.C_UPDATES_LIST_EXTRA_PROP_NAME);

        try {
            for (final ResponseUpdateItemInterface update : updates) {
                if (update != null) m_pendingUpdates.put(update);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public enum OperationType {
        START_UPDATE_CHECKER(1), PROCESS_RECEIVED_UPDATES(2);

        private int m_id = 0;

        private OperationType(final int id) {
            m_id = id;
        }

        public int getId() {
            return m_id;
        }

        public static OperationType getOperationTypeById(final int id) {
            for (final OperationType operationType : OperationType.values())
                if (operationType.m_id == id)
                    return operationType;

            return null;
        }
    }
}
