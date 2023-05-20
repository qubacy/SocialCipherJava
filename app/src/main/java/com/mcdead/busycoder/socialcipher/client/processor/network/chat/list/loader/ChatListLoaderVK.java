package com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader;

import static com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext.C_REQUEST_TIMEOUT;

import android.os.Process;
import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListItemGroup;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListItemUserProfile;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListWrapper;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityConversation;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public class ChatListLoaderVK extends ChatListLoaderBase {
    final protected VKAPIChat m_vkAPIChat;

    protected ChatListLoaderVK(
            final String token,
            final ChatTypeDefinerVK chatTypeDefiner,
            final ChatListLoadingCallback callback,
            final UserLoaderSyncVK userLoader,
            final MessageProcessorVK messageProcessor,
            final VKAPIChat vkAPIChat)
    {
        super(token, chatTypeDefiner, callback, userLoader, messageProcessor);

        m_vkAPIChat = vkAPIChat;
    }

    private Error initUserData(final List<ResponseChatListItemUserProfile> usersData,
                               final List<ResponseChatListItemGroup> groupsData)
    {
        if (usersData == null || groupsData == null)
            return new Error("Users / Groups data is empty!", true);

        for (final ResponseChatListItemUserProfile userData : usersData) {
            UserEntity user =
                    UserEntityGenerator.generateUserEntity(
                        userData.id,
                        userData.firstName + " " + userData.lastName);

            if (user == null)
                return new Error("New User's creation process has been failed!", true);

            if (!UsersStore.getInstance().addUser(user))
                return new Error("User init. error!", true);
        }

        for (final ResponseChatListItemGroup groupData : groupsData) {
            UserEntity user =
                    UserEntityGenerator.generateUserEntity(
                            -groupData.id,
                            groupData.name);

            if (user == null)
                return new Error("New User's creation process has been failed!", true);

            if (!UsersStore.getInstance().addUser(user))
                return new Error("User init. error!", true);
        }

        return null;
    }

    private Error initChatsContent(
            final ResponseChatListBody chatListResponse)
            throws IOException
    {
        if (m_chatTypeDefiner == null)
            return new Error("Dialog type definer is not initialized!", true);

        for (final ResponseChatListItem chatItem : chatListResponse.items) {
            SystemClock.sleep(C_REQUEST_TIMEOUT);

            ChatEntity chat =
                    ChatEntityGenerator.generateChatByType(
                        m_chatTypeDefiner.getDialogType(chatItem),
                        chatItem.conversation.peer.id);

            Response<ResponseChatContentWrapper> messagesResponse =
                    m_vkAPIChat.getChatContent(
                        chatItem.conversation.peer.id,
                        m_token).execute();

            if (!messagesResponse.isSuccessful())
                return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
            if (messagesResponse.body().error != null)
                return new Error(messagesResponse.body().error.message, true);
            if (!ChatsStore.getInstance().addChat(chat))
                return new Error("Dialog init. error!", true);

            if (chat.getType() == ChatType.CONVERSATION) {
                ChatEntityConversation chatConversation = (ChatEntityConversation) chat;

                chatConversation.setTitle(chatItem.conversation.chatSettings.title);
            }

            List<ResponseChatContentItem> messagesItems = messagesResponse.body().response.items;

            Collections.reverse(messagesItems);

            UsersStore usersStore = UsersStore.getInstance();

            if (usersStore == null)
                return new Error("Users' Store hasn't been initialized yet!", true);

            for (final ResponseChatContentItem messageItem : messagesItems) {
                UserEntity senderUser = usersStore.getUserByPeerId(messageItem.fromId);

                if (senderUser == null) {
                    SystemClock.sleep(C_REQUEST_TIMEOUT);

                    Error loadingNewUserError = m_userLoader.loadUserById(messageItem.fromId);

                    if (loadingNewUserError != null)
                        return loadingNewUserError;

                    senderUser = usersStore.getUserByPeerId(messageItem.fromId);

                    if (senderUser == null)
                        return new Error("Message's Sender hasn't been loaded yet!", true);
                }

                ObjectWrapper<MessageEntity> newMessageWrapper = new ObjectWrapper<>();
                Error error =
                        m_messageProcessor.processReceivedMessage(
                            messageItem,
                            chat.getDialogId(),
                            senderUser,
                            newMessageWrapper);

                if (error != null)
                    return error;

                if (!chat.addMessage(newMessageWrapper.getValue()))
                    return new Error("Dialog message init. error!", true);
            }
        }

        return null;
    }

    @Override
    protected Error doInBackground(Void... voids) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Error error = null;

        try {
            Response<ResponseChatListWrapper> response =
                    m_vkAPIChat.getChatList(m_token).execute();

            if (!response.isSuccessful())
                return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            error = initUserData(
                    response.body().response.profiles,
                    response.body().response.groups);

            if (error != null) return error;

            error = initChatsContent(response.body().response);

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
