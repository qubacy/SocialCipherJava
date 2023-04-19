package com.mcdead.busycoder.socialcipher.processor.network.update.processor;

import static com.mcdead.busycoder.socialcipher.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.C_NEW_MESSAGE_CHAT_ID_PROP_NAME;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.data.ResponseChatDataBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.data.ResponseChatDataWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.group.ResponseGroupContext;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityWithGroup;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityDialog;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatType;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.processor.user.loader.UserLoaderSyncFactory;
import com.mcdead.busycoder.socialcipher.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit2.Response;

public class UpdateProcessorAsyncVK extends UpdateProcessorAsyncBase {
    public static final long C_MESSAGE_PROCESS_TIMEOUT = 500;

    private ChatTypeDefinerVK m_dialogTypeDefiner = null;
    private UserLoaderSyncVK m_userLoader = null;

    public UpdateProcessorAsyncVK(final String token,
                                  Context context,
                                  LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue)
    {
        super(token, context, updateQueue);
    }

    private Error init() {
        m_dialogTypeDefiner = (ChatTypeDefinerVK) ChatTypeDefinerFactory.generateDialogTypeDefiner();
        m_userLoader = (UserLoaderSyncVK) UserLoaderSyncFactory.generateUserLoader();

        if (m_dialogTypeDefiner == null)
            return new Error("DialogTypeDefiner hasn't been initialized!", true);
        if (m_userLoader == null)
            return new Error("UserLoader hasn't been initialized!", true);

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

        ChatEntity dialogEntity = ChatsStore.getInstance().getChatByPeerId(updateItem.chatId);

        if (dialogEntity == null) {
            Error newChatProcessingError = processNewChat(updateItem.chatId);

            if (newChatProcessingError != null)
                return newChatProcessingError;
        }

        if (!ChatsStore.getInstance().addNewMessage(newMessageWrapper.getValue(), updateItem.chatId))
            return new Error("New message addition error!", true);

        Error attachmentsError = messageProcessor.processMessageAttachments(
                newMessageWrapper.getValue(), updateItem.chatId);

        if (attachmentsError != null) return attachmentsError;

        Intent intent
                = new Intent(m_context.getApplicationContext(), ChatListBroadcastReceiver.class)
                .setAction(ChatListBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intent.putExtra(C_NEW_MESSAGE_CHAT_ID_PROP_NAME, updateItem.chatId);

        LocalBroadcastManager.getInstance(m_context).sendBroadcast(intent);

        return null;
    }

    private Error processNewChat(
            final long chatId)
    {
        ChatType dialogType = m_dialogTypeDefiner.getDialogTypeByPeerId(chatId);

        if (dialogType == null)
            return new Error("Unknown dialog type!", true);

        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new Error("API hasn't been initialized!", true);

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        switch (dialogType) {
            case DIALOG: return initChatUser(vkAPIChat, chatId);
            case WITH_GROUP: return initChatGroup(vkAPIChat, chatId);
            case CONVERSATION: return initChatConversation(vkAPIChat, chatId);
        }

        return null;
    }

    private Error initChatUser(
            final VKAPIChat vkAPIChat,
            final long chatId)
    {
        if (chatId == 0)
            return new Error("Incorrect chatId has been provided!", true);

        ChatEntityDialog chatUserEntity = new ChatEntityDialog(chatId);
        Error userLoadingError = m_userLoader.loadUserById(chatId);

        if (userLoadingError != null)
            return userLoadingError;

        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);
        if (!dialogsStore.addChat(chatUserEntity))
            return new Error("New Chat adding operation has been failed!", true);

        return null;
    }

    private Error initChatGroup(
            final VKAPIChat vkAPIChat,
            final long chatId)
    {
        if (ResponseGroupContext.isChatGroupId(chatId))
            return new Error("Incorrect chatId has been provided!", true);

        ChatEntityWithGroup chatGroupEntity = new ChatEntityWithGroup(chatId);
        Error userLoadingError = m_userLoader.loadUserById(chatId);

        if (userLoadingError != null)
            return userLoadingError;

        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);
        if (!dialogsStore.addChat(chatGroupEntity))
            return new Error("New Chat adding operation has been failed!", true);

        return null;
    }

    private Error initChatConversation(
            final VKAPIChat vkAPIChat,
            final long chatId)
    {
        if (!ResponseChatContext.isChatConversationId(chatId))
            return new Error("Incorrect chatId has been provided!", true);

        ChatEntityConversation conversationEntity = null;

        try {
            Response<ResponseChatDataWrapper> response =
                    vkAPIChat.getChatData(
                            ResponseChatContext.getLocalChatIdByPeerId(chatId),
                            m_token).execute();

            if (!response.isSuccessful())
                return new Error("Chat Data Request has been failed!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            ResponseChatDataBody responseChatBody = response.body().response;

            conversationEntity =
                    new ChatEntityConversation(
                            chatId,
                            responseChatBody.title,
                            responseChatBody.userIdList);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        for (final long userId : conversationEntity.getUsersList()) {
            Error userLoadingError = m_userLoader.loadUserById(userId);

            if (userLoadingError == null) continue;

            return userLoadingError;
        }

        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);
        if (!dialogsStore.addChat(conversationEntity))
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
