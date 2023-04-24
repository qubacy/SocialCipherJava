package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.ResponseChatContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataWrapper;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.chattype.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class ChatLoaderVK extends ChatLoaderBase {
    private UserLoaderSyncVK m_userLoader = null;

    public ChatLoaderVK(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId)
    {
        super(token, callback, chatId);
    }

    public Error init() {
        m_userLoader = (UserLoaderSyncVK) UserLoaderSyncFactory.generateUserLoader();

        if (m_userLoader == null)
            return new Error("UserLoader hasn't been initialized!", true);

        return null;
    }

    @Override
    protected Error doInBackground(Void... voids) {
        Error initError = init();

        if (initError != null) return initError;

        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("DialogsStore hasn't been initialized!", true);

        ChatEntity dialog = dialogsStore.getChatByPeerId(m_chatId);

        if (dialog == null)
            return new Error("DialogEntity hasn't been found!", true);

        List<MessageEntity> messages = dialog.getMessages();

        if (messages == null)
            return new Error("Messages' list hasn't been initialized!", true);

        MessageProcessorVK messageProcessorVK
                = (MessageProcessorVK) MessageProcessorStore.getProcessor();

        if (messageProcessorVK == null)
            return new Error("Message Processor hasn't been initialized!", true);

        for (final MessageEntity message : messages) {
            Error error = messageProcessorVK.processMessageAttachments(message, m_chatId);

            if (error != null) return error;
        }

        if (dialog.getType() == ChatType.CONVERSATION) {
            ChatEntityConversation chatEntity = (ChatEntityConversation) dialog;

            if (!chatEntity.getUsersList().isEmpty()) {
                Error chatLoadingError = loadChatUsers(dialogsStore);

                if (chatLoadingError != null) return chatLoadingError;
            }
        }

        return null;
    }

    private Error loadChatUsers(
            final ChatsStore dialogsStore)
    {
        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new Error("API hasn't been initialized!", true);

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();
        ResponseChatDataBody responseChatBody = null;

        try {
            Response<ResponseChatDataWrapper> chatResponse =
                    vkAPIChat.getChatData(
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

        for (final long userId : responseChatBody.userIdList) {
            Error userLoadingError = m_userLoader.loadUserById(userId);

            if (userLoadingError != null) return userLoadingError;

            SystemClock.sleep(VKAPIContext.C_REQUEST_TIMEOUT);
        }

        ChatEntityConversation chatEntity =
                (ChatEntityConversation) dialogsStore.getChatByPeerId(m_chatId);

        if (chatEntity == null)
            return new Error("Retrieved Chat Entity was null!", true);
        if (!chatEntity.setUsersList(responseChatBody.userIdList))
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
