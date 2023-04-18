package com.mcdead.busycoder.socialcipher.dialog.dialogloader;

import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.chat.ResponseChatWrapper;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityConversation;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.userloadersync.UserLoaderSyncFactory;
import com.mcdead.busycoder.socialcipher.userloadersync.UserLoaderSyncVK;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class DialogLoaderVK extends DialogLoaderBase {
    private UserLoaderSyncVK m_userLoader = null;

    public DialogLoaderVK(
            final String token,
            final DialogLoadingCallback callback,
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

        DialogsStore dialogsStore = DialogsStore.getInstance();

        if (dialogsStore == null)
            return new Error("DialogsStore hasn't been initialized!", true);

        DialogEntity dialog = dialogsStore.getDialogByPeerId(m_chatId);

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

        if (dialog.getType() == DialogType.CONVERSATION) {
            DialogEntityConversation chatEntity = (DialogEntityConversation) dialog;

            if (!chatEntity.getUsersList().isEmpty()) {
                Error chatLoadingError = loadChatUsers(dialogsStore);

                if (chatLoadingError != null) return chatLoadingError;
            }
        }

        return null;
    }

    private Error loadChatUsers(
            final DialogsStore dialogsStore)
    {
        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("API hasn't been initialized!", true);

        ResponseChatBody responseChatBody = null;

        try {
            Response<ResponseChatWrapper> chatResponse =
                    vkAPI.chat(ResponseChatContext.getLocalChatIdByPeerId(m_chatId), m_token).execute();

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

        DialogEntityConversation chatEntity =
                (DialogEntityConversation) dialogsStore.getDialogByPeerId(m_chatId);

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
