package com.mcdead.busycoder.socialcipher.dialog.dialogloader;

import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorVK;

import java.util.List;

public class DialogLoaderVK extends DialogLoaderBase {
    public DialogLoaderVK(
            final String token,
            final DialogLoadingCallback callback,
            final long chatId)
    {
        super(token, callback, chatId);
    }

    @Override
    protected Error doInBackground(Void... voids) {
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
