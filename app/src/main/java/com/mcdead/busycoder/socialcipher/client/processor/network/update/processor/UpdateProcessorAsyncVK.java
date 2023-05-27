package com.mcdead.busycoder.socialcipher.client.processor.network.update.processor;

import static com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.C_NEW_MESSAGE_CHAT_ID_PROP_NAME;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.ResponseChatContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.group.ResponseGroupContext;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityWithGroup;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityDialog;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.commandchecker.CommandMessageRetriever;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.processor.data.AttachmentProcessingResult;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorServiceBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit2.Response;

public class UpdateProcessorAsyncVK extends UpdateProcessorAsyncBase {
    public static final long C_MESSAGE_PROCESS_TIMEOUT = 500;

    final private ChatTypeDefinerVK m_chatTypeDefiner;
    final private UserLoaderSyncVK m_userLoader;

    final VKAPIChat m_vkAPIChat;
    final MessageProcessorVK m_messageProcessor;

    protected UpdateProcessorAsyncVK(
            final String token,
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue,
            final ChatTypeDefinerVK chatTypeDefiner,
            final UserLoaderSyncVK userLoader,
            final VKAPIChat vkAPIChat,
            final MessageProcessorVK messageProcessor)
    {
        super(token, context, updateQueue);

        m_chatTypeDefiner = chatTypeDefiner;
        m_userLoader = userLoader;

        m_vkAPIChat = vkAPIChat;
        m_messageProcessor = messageProcessor;
    }

    public static UpdateProcessorAsyncVK getInstance(
            final String token,
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue,
            final ChatTypeDefinerVK chatTypeDefiner,
            final UserLoaderSyncVK userLoader,
            final VKAPIChat vkAPIChat,
            final MessageProcessorVK messageProcessor)
    {
        if (token == null || context == null || updateQueue == null || chatTypeDefiner == null ||
            userLoader == null || vkAPIChat == null || messageProcessor == null)
        {
            return null;
        }
        if (token.isEmpty()) return null;

        return new UpdateProcessorAsyncVK(
                token, context, updateQueue, chatTypeDefiner,
                userLoader, vkAPIChat, messageProcessor);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

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
        ChatsStore chatsStore = ChatsStore.getInstance();

        if (chatsStore == null)
            return new Error("Chats' Store hasn't been initialized yet!", true);

        ChatEntity chatEntity = chatsStore.getChatById(updateItem.chatId);

        if (chatEntity == null) {
            Error newChatProcessingError = processNewChat(updateItem.chatId);

            if (newChatProcessingError != null)
                return newChatProcessingError;
        }

        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null)
            return new Error("Users' Store hasn't been initialized yet!", true);

        if (usersStore.getUserByPeerId(updateItem.fromPeerId) == null) {
            Error userLoadingError = m_userLoader.loadUserById(updateItem.fromPeerId);

            if (userLoadingError != null)
                return userLoadingError;
        }

        UserEntity senderUser = usersStore.getUserByPeerId(updateItem.fromPeerId);

        if (senderUser == null)
            return new Error("Message's Sender hasn't been loaded!", true);

        ObjectWrapper<MessageEntity> newMessageWrapper = new ObjectWrapper<>();
        Error error =
                m_messageProcessor.processReceivedUpdateMessage(
                        updateItem,
                        updateItem.chatId,
                        senderUser,
                        newMessageWrapper);

        if (error != null) return error;

        ObjectWrapper<Boolean> successFlagWrapper = new ObjectWrapper<>(false);
        Error retrievingCommandError =
                checkMessageForCommand(
                        updateItem.chatId,
                        newMessageWrapper.getValue(),
                        successFlagWrapper);

        if (retrievingCommandError != null)
            return retrievingCommandError;

        if (successFlagWrapper.getValue()) {
            // todo: is there something to do here??

            return null;
        }

        if (!ChatsStore.getInstance().addNewMessage(newMessageWrapper.getValue(), updateItem.chatId))
            return new Error("New message addition error!", true);

        ObjectWrapper<AttachmentProcessingResult> attachmentProcessingResultWrapper =
                new ObjectWrapper<>();
        Error attachmentsError =
                m_messageProcessor.processMessageAttachments(
                        newMessageWrapper.getValue(),
                        updateItem.chatId,
                        attachmentProcessingResultWrapper);

        if (attachmentsError != null) return attachmentsError;

        if (attachmentProcessingResultWrapper.getValue() != null) {
            if (!chatsStore.setMessageAttachments(
                    attachmentProcessingResultWrapper.getValue().getLoadedAttachmentList(),
                    updateItem.chatId,
                    updateItem.messageId,
                    attachmentProcessingResultWrapper.getValue().isCiphered())) {
                return new Error("Setting attachments to message process went wrong!", true);
            }
        }

        Intent intent
                = new Intent(m_context.getApplicationContext(), ChatListBroadcastReceiver.class)
                .setAction(ChatListBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intent.putExtra(C_NEW_MESSAGE_CHAT_ID_PROP_NAME, updateItem.chatId);

        LocalBroadcastManager.getInstance(m_context).sendBroadcast(intent);

        return null;
    }

    private Error checkMessageForCommand(
            final long chatId,
            final MessageEntity messageEntity,
            ObjectWrapper<Boolean> successFlagWrapper)
    {
        CommandMessageRetriever commandMessageRetriever =
                CommandMessageRetriever.getInstance(
                        chatId,
                        messageEntity.getSenderUser().getPeerId(),
                        messageEntity.getId());

        if (commandMessageRetriever == null)
            return new Error("Command Retriever generation error!", true);

        CommandMessage commandMessage =
                commandMessageRetriever.retrieveCommandMessage(messageEntity.getMessage());

        if (commandMessage == null) {
            successFlagWrapper.setValue(false);

            return null;
        }

        // todo: conveying the new command to the processor...

        Intent intent = new Intent(
                CommandProcessorServiceBroadcastReceiver.C_PROCESS_COMMAND_MESSAGE);

        intent.putExtra(
                CommandProcessorServiceBroadcastReceiver.C_COMMAND_MESSAGE_PROP_NAME,
                commandMessage);

        LocalBroadcastManager.
                getInstance(m_context.getApplicationContext()).
                sendBroadcast(intent);

        successFlagWrapper.setValue(true);

        return null;
    }

    private Error processNewChat(
            final long chatId)
    {
        ChatType chatType = m_chatTypeDefiner.getChatTypeByPeerId(chatId);

        if (chatType == null)
            return new Error("Unknown dialog type!", true);

        switch (chatType) {
            case DIALOG: return initChatUser(chatId);
            case WITH_GROUP: return initChatGroup(chatId);
            case CONVERSATION: return initChatConversation(chatId);
        }

        return null;
    }

    private Error initChatUser(
            final long chatId)
    {
        if (chatId == 0)
            return new Error("Incorrect chatId has been provided!", true);

        ChatEntityDialog chatUserEntity =
                (ChatEntityDialog) ChatEntityGenerator.generateChatByType(ChatType.DIALOG, chatId);
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
            final long chatId)
    {
        if (ResponseGroupContext.isChatGroupId(chatId))
            return new Error("Incorrect chatId has been provided!", true);

        ChatEntityWithGroup chatGroupEntity =
                (ChatEntityWithGroup) ChatEntityGenerator.
                        generateChatByType(ChatType.WITH_GROUP, chatId);
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
            final long chatId)
    {
        if (!ResponseChatContext.isChatConversationId(chatId))
            return new Error("Incorrect chatId has been provided!", true);

        ChatEntityConversation conversationEntity = null;
        List<Long> userIdList = null;

        try {
            Response<ResponseChatDataWrapper> response =
                    m_vkAPIChat.getChatData(
                            ResponseChatContext.getLocalChatIdByPeerId(chatId),
                            m_token).execute();

            if (!response.isSuccessful())
                return new Error("Chat Data Request has been failed!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            ResponseChatDataBody responseChatBody = response.body().response;

            userIdList = responseChatBody.userIdList;

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        List<UserEntity> chatUserList = new LinkedList<>();

        for (final long userId : userIdList) {
            Error userLoadingError = m_userLoader.loadUserById(userId);

            if (userLoadingError == null) continue;

            UserEntity addedUser = UsersStore.getInstance().getUserByPeerId(userId);

            if (addedUser == null)
                return new Error("Added User doesn't exist anymore!", true);

            chatUserList.add(addedUser);

            return userLoadingError;
        }

        conversationEntity =
                (ChatEntityConversation) ChatEntityGenerator.
                        generateChatByType(ChatType.CONVERSATION, chatId);
        conversationEntity.setUsersList(chatUserList);

        ChatsStore chatsStore = ChatsStore.getInstance();

        if (chatsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);
        if (!chatsStore.addChat(conversationEntity))
            return new Error("New Chat adding operation has been failed!", true);

        return null;
    }

    public enum EventType {
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
