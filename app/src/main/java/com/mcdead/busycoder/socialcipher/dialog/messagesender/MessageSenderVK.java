package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseSendMessageWrapper;
import com.mcdead.busycoder.socialcipher.error.Error;

import java.io.IOException;

import retrofit2.Response;

public class MessageSenderVK extends MessageSenderBase {

    public MessageSenderVK(
            final String token,
            final long peerId,
            final String text,
            final MessageSendingCallback callback)
    {
        super(token, peerId, text, callback);
    }

    @Override
    protected Error doInBackground(Void... voids) {
        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("API hasn't been initialized!", true);

        try {
            // todo: work with attachments!!
            Response<ResponseSendMessageWrapper> response
                    = vkAPI.sendMessage(m_token, m_peerId, m_text, "").execute();

            if (!response.isSuccessful())
                return new Error("Message Sending request hasn't been accomplished!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Error error) {
        if (error == null)
            m_callback.onMessageSent();
        else
            m_callback.onMessageSendingError(error);
    }
}
