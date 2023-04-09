package com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader;

import android.os.AsyncTask;
import android.os.Process;
import android.os.SystemClock;

import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsItemGroup;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsItemUserProfile;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsWrapper;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.DialogGenerator;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntityConversation;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public class DialogsLoaderVK extends DialogsLoaderBase {
    public DialogsLoaderVK(final String token,
                           final DialogTypeDefinerVK dialogTypeDefiner,
                           final DialogsLoadingCallback callback)
    {
        super(token, dialogTypeDefiner, callback);
    }

    private Error initUserData(final List<ResponseDialogsItemUserProfile> usersData,
                               final List<ResponseDialogsItemGroup> groupsData)
    {
        if (usersData == null || groupsData == null)
            return new Error("Users / Groups data is empty!", true);

        for (final ResponseDialogsItemUserProfile userData : usersData) {
            UserEntity user = new UserEntity(userData.id, userData.firstName + " " + userData.lastName);

            if (!UsersStore.getInstance().addUser(user))
                return new Error("User init. error!", true);
        }

        for (final ResponseDialogsItemGroup groupData : groupsData) {
            UserEntity user = new UserEntity(-groupData.id, groupData.name);

            if (!UsersStore.getInstance().addUser(user))
                return new Error("User init. error!", true);
        }

        return null;
    }

    private Error initDialogsContent(
            final VKAPIInterface apiInterface,
            final MessageProcessorVK messageProcessor,
            final ResponseDialogsBody dialogsResponse)
            throws IOException
    {
        if (m_dialogTypeDefiner == null)
            return new Error("Dialog type definer is not initialized!", true);

        for (final ResponseDialogsItem dialogItem : dialogsResponse.items) {
            SystemClock.sleep(VKAPIContext.C_REQUEST_TIMEOUT);

            DialogEntity dialog = DialogGenerator.generateDialogByType(
                    m_dialogTypeDefiner.getDialogType(dialogItem),
                    dialogItem.conversation.peer.id);

            Response<ResponseDialogWrapper> messagesResponse = apiInterface.dialog(
                    dialogItem.conversation.peer.id,
                    m_token).execute();

            if (!messagesResponse.isSuccessful())
                return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
            if (messagesResponse.body().error != null)
                return new Error(messagesResponse.body().error.message, true);
            if (!DialogsStore.getInstance().addDialog(dialog))
                return new Error("Dialog init. error!", true);

            if (dialog.getType() == DialogType.CONVERSATION) {
                ((DialogEntityConversation)dialog).setTitle(dialogItem.conversation.chatSettings.title);

                // todo: init. all members..
            }



//            if (dialog.getType() == DialogType.USER) {
//                Error userError;
//
//                if ((userError = loadUserData(dialog.getPeerId())) != null)
//                    return userError;
//            }

            List<ResponseDialogItem> messagesItems = messagesResponse.body().response.items;

            Collections.reverse(messagesItems);

            for (final ResponseDialogItem messageItem : messagesItems) {
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

        try {
            VKAPIInterface apiInterface = (VKAPIInterface) APIStore.getAPIInstance();

            if (apiInterface == null)
                return new Error("API Interface obj. should be initialized first!", true);

            Response<ResponseDialogsWrapper> response = apiInterface.dialogs(m_token).execute();

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

            error = initDialogsContent(apiInterface, messageProcessor, response.body().response);

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
