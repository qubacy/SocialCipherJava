package com.mcdead.busycoder.socialcipher.updateprocessor;

import static com.mcdead.busycoder.socialcipher.dialoglist.DialogsBroadcastReceiver.C_NEW_MESSAGE_CHAT_ID_PROP_NAME;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatWrapper;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityConversation;
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
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit2.Response;

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

        ObjectWrapper<MessageEntity> newMessageWrapper = new ObjectWrapper<>();
        Error error = messageProcessor.processReceivedUpdateMessage(
                updateItem, updateItem.chatId, newMessageWrapper);

        if (error != null) return error;

        DialogEntity dialogEntity = DialogsStore.getInstance().getDialogByPeerId(updateItem.chatId);

        if (dialogEntity == null) {
            Error newChatProcessingError = processNewChat(updateItem.chatId);

            if (newChatProcessingError != null)
                return newChatProcessingError;
        }

        if (!DialogsStore.getInstance().addNewMessage(newMessageWrapper.getValue(), updateItem.chatId))
            return new Error("New message addition error!", true);

        Error attachmentsError = messageProcessor.processMessageAttachments(
                newMessageWrapper.getValue(), updateItem.chatId);

        if (attachmentsError != null) return attachmentsError;

        Intent intent
                = new Intent(m_context.getApplicationContext(), DialogsBroadcastReceiver.class)
                .setAction(DialogsBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intent.putExtra(C_NEW_MESSAGE_CHAT_ID_PROP_NAME, updateItem.chatId);

        LocalBroadcastManager.getInstance(m_context).sendBroadcast(intent);

        return null;
    }

    private Error processNewChat(
            final long chatId)
    {
        DialogType dialogType = m_dialogTypeDefiner.getDialogTypeByPeerId(chatId);

        if (dialogType == null)
            return new Error("Unknown dialog type!", true);

        Error initChatError = initChatData(chatId);

        if (initChatError != null) return initChatError;

        if (!DialogsStore.getInstance().addDialog(DialogGenerator.generateChatByType(
                dialogType, chatId
        )))
        {
            return new Error("New dialog addition error!", true);
        }

        return null;
    }

    private Error initChatData(
            final long chatId)
    {
        if (chatId == 0)
            return new Error("Incorrect chatId has been provided!", true);

        // todo: getting chat data.. (including its users)

        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("VK API hasn't been initialized!", true);

        DialogEntityConversation chatEntity = null;

        try {
            Response<ResponseChatWrapper> response =
                    vkAPI.chat(
                            ResponseChatContext.getLocalChatIdByPeerId(chatId),
                            m_token).execute();

            if (!response.isSuccessful())
                return new Error("Chat Data Request has been failed!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            ResponseChatBody responseChatBody = response.body().response;

            chatEntity =
                    new DialogEntityConversation(
                            chatId,
                            responseChatBody.title,
                            responseChatBody.userIdList);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        // todo: load all chat users..


        DialogsStore dialogsStore = DialogsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);
        if (!dialogsStore.addDialog(chatEntity))
            return new Error("New Chat adding operation has been failed!", true);

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
