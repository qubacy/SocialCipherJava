package com.mcdead.busycoder.socialcipher.processor.chat.list.loader;

import android.os.Process;
import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.content.ResponseChatContentItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.content.ResponseChatContentWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list.ResponseChatListBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list.ResponseChatListItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list.ResponseChatListItemGroup;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list.ResponseChatListItemUserProfile;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list.ResponseChatListWrapper;
import com.mcdead.busycoder.socialcipher.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatType;
import com.mcdead.busycoder.socialcipher.data.entity.chat.chattype.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.utility.chat.ChatGenerator;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public class ChatListLoaderVK extends ChatListLoaderBase {
    public ChatListLoaderVK(final String token,
                            final ChatTypeDefinerVK dialogTypeDefiner,
                            final ChatListLoadingCallback callback)
    {
        super(token, dialogTypeDefiner, callback);
    }

    private Error initUserData(final List<ResponseChatListItemUserProfile> usersData,
                               final List<ResponseChatListItemGroup> groupsData)
    {
        if (usersData == null || groupsData == null)
            return new Error("Users / Groups data is empty!", true);

        for (final ResponseChatListItemUserProfile userData : usersData) {
            UserEntity user = new UserEntity(userData.id, userData.firstName + " " + userData.lastName);

            if (!UsersStore.getInstance().addUser(user))
                return new Error("User init. error!", true);
        }

        for (final ResponseChatListItemGroup groupData : groupsData) {
            UserEntity user = new UserEntity(-groupData.id, groupData.name);

            if (!UsersStore.getInstance().addUser(user))
                return new Error("User init. error!", true);
        }

        return null;
    }

    private Error initDialogsContent(
            final VKAPIChat vkAPIChat,
            final MessageProcessorVK messageProcessor,
            final ResponseChatListBody dialogsResponse)
            throws IOException
    {
        if (m_dialogTypeDefiner == null)
            return new Error("Dialog type definer is not initialized!", true);

        for (final ResponseChatListItem dialogItem : dialogsResponse.items) {
            SystemClock.sleep(VKAPIContext.C_REQUEST_TIMEOUT);

            ChatEntity dialog = ChatGenerator.generateChatByType(
                    m_dialogTypeDefiner.getDialogType(dialogItem),
                    dialogItem.conversation.peer.id);

            Response<ResponseChatContentWrapper> messagesResponse =
                    vkAPIChat.getChatContent(
                        dialogItem.conversation.peer.id,
                        m_token).execute();

            if (!messagesResponse.isSuccessful())
                return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
            if (messagesResponse.body().error != null)
                return new Error(messagesResponse.body().error.message, true);
            if (!ChatsStore.getInstance().addChat(dialog))
                return new Error("Dialog init. error!", true);

            if (dialog.getType() == ChatType.CONVERSATION) {
                ChatEntityConversation chatConversation = (ChatEntityConversation) dialog;

                chatConversation.setTitle(dialogItem.conversation.chatSettings.title);
            }

            List<ResponseChatContentItem> messagesItems = messagesResponse.body().response.items;

            Collections.reverse(messagesItems);

            for (final ResponseChatContentItem messageItem : messagesItems) {
                ObjectWrapper<MessageEntity> newMessageWrapper = new ObjectWrapper<>();
                Error error = messageProcessor.processReceivedMessage(
                        messageItem,
                        dialog.getDialogId(),
                        newMessageWrapper);

                if (error != null)
                    return error;

                if (!dialog.addMessage(newMessageWrapper.getValue()))
                    return new Error("Dialog message init. error!", true);
            }
        }

        return null;
    }

    @Override
    protected Error doInBackground(Void... voids) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Error error = null;
        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new Error("API hasn't been initialized!", true);

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        try {
            Response<ResponseChatListWrapper> response = vkAPIChat.getChatList(m_token).execute();

            if (!response.isSuccessful())
                return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            error = initUserData(
                    response.body().response.profiles,
                    response.body().response.groups);

            if (error != null) return error;

            MessageProcessorVK messageProcessor = (MessageProcessorVK) MessageProcessorStore.getProcessor();

            if (messageProcessor == null)
                return new Error("MessageProcessor obj. should be initialized first!", true);

            error = initDialogsContent(vkAPIChat, messageProcessor, response.body().response);

            if (error != null) return error;

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(VKAPIContext.C_REQUEST_CRASHED_MESSAGE, true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Error error) {
        if (error == null)
            m_callback.onDialogsLoaded();
        else
            m_callback.onDialogsLoadingError(error);
    }
}
