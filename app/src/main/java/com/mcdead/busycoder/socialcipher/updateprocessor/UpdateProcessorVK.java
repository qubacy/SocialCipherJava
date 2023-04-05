package com.mcdead.busycoder.socialcipher.updateprocessor;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.DialogGenerator;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.dialoglist.DialogsBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorVK;

import java.util.concurrent.LinkedBlockingQueue;

public class UpdateProcessorVK extends UpdateProcessorBase {
    public static final long C_MESSAGE_PROCESS_TIMEOUT = 500;

    private DialogTypeDefinerVK m_dialogTypeDefiner = null;

    public UpdateProcessorVK(final String token,
                             Context context,
                             LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue)
    {
        super(token, context, updateQueue);
    }

    private Error init() {
        m_dialogTypeDefiner = (DialogTypeDefinerVK) DialogTypeDefinerFactory.generateDialogTypeDefiner();

        if (m_dialogTypeDefiner == null)
            return new Error("DialogTypeDefiner hasn't been initialized!", true);

        return null;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Error initError = null;

        if ((initError = init()) != null)
            processError(initError);

        while (!Thread.interrupted()) {
            SystemClock.sleep(C_MESSAGE_PROCESS_TIMEOUT);

            ResponseUpdateItem updateItem = (ResponseUpdateItem) m_updateQueue.poll();

            if (updateItem == null) continue;

            Error err = null;

            if ((err = processUpdate(updateItem)) != null)
                processError(err);
        }
    }

    private void processError(final Error error) {
        Intent intent = new Intent(m_context.getApplicationContext(), ErrorBroadcastReceiver.class)
                .setAction(ErrorBroadcastReceiver.C_ERROR_RECEIVED);

        intent.putExtra(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME, error);

        LocalBroadcastManager.getInstance(m_context).sendBroadcast(intent);
    }

    private Error processUpdate(final ResponseUpdateItem updateItem) {
        EventType eventType = EventType.getEventTypeById(updateItem.eventType);

        if (eventType == null)
            new Error("Unknown update type!", false);

        switch (eventType) {
            case NEW_MESSAGE: return processNewMessageUpdate(updateItem);
        }

        return new Error("No handler for the specified update!", false);
    }

    private Error processNewMessageUpdate(
            final ResponseUpdateItem updateItem)
    {
        MessageProcessorVK messageProcessor = (MessageProcessorVK) MessageProcessorStore.getProcessor();

        if (messageProcessor == null)
            return new Error("Message processor has not been initialized yet!", true);

        MessageEntity newMessage = messageProcessor.processReceivedUpdateMessage(updateItem, updateItem.chatId);

        if (newMessage == null)
            return new Error("Message processing error has been occurred!", true);

        DialogEntity dialogEntity = DialogsStore.getInstance().getDialogByPeerId(newMessage.getFromPeerId());

        if (dialogEntity == null) {
            DialogType dialogType = m_dialogTypeDefiner.getDialogTypeByPeerId(newMessage.getFromPeerId());

            if (dialogType == null)
                return new Error("Unknown dialog type!", true);

            if (!DialogsStore.getInstance().addDialog(DialogGenerator.generateDialogByType(
                    dialogType, newMessage.getFromPeerId()
            )))
            {
                return new Error("New dialog addition error!", true);
            }
        }

        if (!DialogsStore.getInstance().addNewMessage(newMessage))
            return new Error("New message processing error!", true);

        Intent intent = new Intent(m_context.getApplicationContext(), DialogsBroadcastReceiver.class)
                .setAction(DialogsBroadcastReceiver.C_NEW_MESSAGES_ADDED);

        LocalBroadcastManager.getInstance(m_context).sendBroadcast(intent);

        return null;
    }

    private enum EventType {
        NEW_MESSAGE(4);

        private int m_eventId = 0;

        private EventType(final int eventId) {
            m_eventId = eventId;
        }

        public int getEventId() {
            return m_eventId;
        }

        public static EventType getEventTypeById(final int eventId) {
            for (final EventType event : EventType.values())
                if (event.m_eventId == eventId) return event;

            return null;
        }
    };
}
