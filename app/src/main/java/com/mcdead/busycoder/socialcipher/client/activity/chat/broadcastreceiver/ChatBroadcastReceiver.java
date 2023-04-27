package com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public class ChatBroadcastReceiver extends BroadcastReceiver {
    public static final String C_NEW_MESSAGE_ADDED = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.NEW_MESSAGE_ADDED";
    public static final String C_SETTING_CIPHER_SESSION_ANSWER_REQUESTED = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.SETTING_CIPHER_SESSION_ANSWER_REQUESTED";

    public static final String C_CHAT_ID_PROP_NAME = "chatId";
    public static final String C_INITIALIZER_PEER_ID_PROP_NAME = "initializerPeerId";

    private ChatBroadcastReceiverCallback m_callback = null;

    public ChatBroadcastReceiver(final ChatBroadcastReceiverCallback callback) {
        super();

        m_callback = callback;
    }

    @Override
    public void onReceive(Context context,
                          Intent intent)
    {
        Error processingError = null;

        switch (intent.getAction()) {
            case C_NEW_MESSAGE_ADDED:
                processingError = processNewMessageAddedAction(); break;
            case C_SETTING_CIPHER_SESSION_ANSWER_REQUESTED:
                processingError = processSettingCipherSessionAnswerRequest(intent); break;
        }

        if (processingError != null)
            m_callback.onChatBroadcastReceiverErrorOccurred(processingError);
    }

    private Error processNewMessageAddedAction() {
        m_callback.onNewChatMessageReceived();

        return null;
    }

    private Error processSettingCipherSessionAnswerRequest(
            final Intent data)
    {
        if (data == null)
            return new Error("Intent data was null!", true);

        long chatId =
                data.getLongExtra(C_CHAT_ID_PROP_NAME, 0);
        long initializerPeerId =
                data.getLongExtra(C_INITIALIZER_PEER_ID_PROP_NAME, 0);

        if (chatId == 0 || initializerPeerId == 0)
            return new Error("Setting Cipher Session Answer Request data was incorrect!", true);

        m_callback.onSettingCipherSessionAnswerRequested(chatId, initializerPeerId);

        return null;
    }
}
