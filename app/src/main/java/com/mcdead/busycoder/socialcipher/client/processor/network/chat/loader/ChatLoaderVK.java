package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.ResponseChatContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataWrapper;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.processor.data.AttachmentProcessingResult;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Response;

public class ChatLoaderVK extends ChatLoaderBase {
    final protected UserLoaderSyncVK m_userLoader;
    final protected VKAPIChat m_vkAPIChat;

    protected ChatLoaderVK(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId,
            final MessageProcessorVK messageProcessor,
            final UserLoaderSyncVK userLoader,
            final VKAPIChat vkAPIChat)
    {
        super(token, callback, chatId, messageProcessor);

        m_userLoader = userLoader;
        m_vkAPIChat = vkAPIChat;
    }

    public static ChatLoaderVK getInstance(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId,
            final MessageProcessorVK messageProcessor,
            final UserLoaderSyncVK userLoader,
            final VKAPIChat vkAPIChat)
    {
        if (token == null || callback == null || messageProcessor == null || userLoader == null ||
            vkAPIChat == null)
        {
            return null;
        }

        ChatIdCheckerVK chatIdCheckerVK = new ChatIdCheckerVK();

        if (!chatIdCheckerVK.isValid(chatId) || token.isEmpty())
            return null;

        return new ChatLoaderVK(token, callback, chatId, messageProcessor, userLoader, vkAPIChat);
    }

    @Override
    protected Error doInBackground(Void... voids) {
        ChatsStore chatsStore = ChatsStore.getInstance();

        if (chatsStore == null)
            return new Error("DialogsStore hasn't been initialized!", true);

        ChatEntity chat = chatsStore.getChatById(m_chatId);

        if (chat == null)
            return new Error("DialogEntity hasn't been found!", true);

        List<MessageEntity> messages = chat.getMessages();

        if (messages == null)
            return new Error("Messages' list hasn't been initialized!", true);

        MessageProcessorVK messageProcessorVK = (MessageProcessorVK) m_messageProcessor;

        for (final MessageEntity message : messages) {
            ObjectWrapper<AttachmentProcessingResult> attachmentProcessingResultWrapper =
                    new ObjectWrapper<>();
            Error error =
                    messageProcessorVK.processMessageAttachments(
                            message, m_chatId, attachmentProcessingResultWrapper);

            if (error != null) return error;
            if (attachmentProcessingResultWrapper.getValue() == null)
                continue;

            if (!chatsStore.setMessageAttachments(
                    attachmentProcessingResultWrapper.getValue().getLoadedAttachmentList(),
                    m_chatId,
                    message.getId(),
                    attachmentProcessingResultWrapper.getValue().isCiphered()))
            {
                return new Error("Setting attachments to message process went wrong!", true);
            }
        }

        if (chat.getType() == ChatType.CONVERSATION) {
            ChatEntityConversation chatEntity = (ChatEntityConversation) chat;

            if (!chatEntity.getUsersList().isEmpty()) {
                Error chatLoadingError = loadChatUsers(chatsStore);

                if (chatLoadingError != null) return chatLoadingError;
            }
        }

        return null;
    }

    private Error loadChatUsers(
            final ChatsStore chatsStore)
    {
        ResponseChatDataBody responseChatBody = null;

        try {
            Response<ResponseChatDataWrapper> chatResponse =
                    m_vkAPIChat.getChatData(
                            ResponseChatContext.getLocalChatIdByPeerId(m_chatId),
                            m_token).execute();

            if (!chatResponse.isSuccessful())
                return new Error("Chat Data Request has been failed!", true);
            if (chatResponse.body().error != null)
                return new Error(chatResponse.body().error.message, true);

            responseChatBody = chatResponse.body().response;

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        List<UserEntity> chatUserList = new LinkedList<>();

        for (final long userId : responseChatBody.userIdList) {
            Error userLoadingError = m_userLoader.loadUserById(userId);

            if (userLoadingError != null) return userLoadingError;

            UserEntity addedUser = UsersStore.getInstance().getUserByPeerId(userId);

            if (addedUser == null)
                return new Error("Added User doesn't exist anymore!", true);

            chatUserList.add(addedUser);

            SystemClock.sleep(VKAPIContext.C_REQUEST_TIMEOUT);
        }

        ChatEntityConversation chatEntity =
                (ChatEntityConversation) chatsStore.getChatById(m_chatId);

        if (chatEntity == null)
            return new Error("Retrieved Chat Entity was null!", true);
        if (!chatEntity.setUsersList(chatUserList))
            return new Error("List of Users setting process has been failed!", true);

        return null;
    }

    @Override
    protected void onPostExecute(Error error) {
        if (error == null)
            m_callback.onDialogLoaded();
        else
            m_callback.onDialogLoadingError(error);
    }
}
