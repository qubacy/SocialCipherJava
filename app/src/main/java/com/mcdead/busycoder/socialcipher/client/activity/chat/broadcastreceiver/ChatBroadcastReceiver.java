package com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public class ChatBroadcastReceiver extends BroadcastReceiver {
    public static final String C_NEW_MESSAGE_ADDED = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.NEW_MESSAGE_ADDED";
    public static final String C_SETTING_CIPHER_SESSION_ANSWER_REQUESTED = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.SETTING_CIPHER_SESSION_ANSWER_REQUESTED";
    public static final String C_SEND_NEW_MESSAGE = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.SEND_NEW_MESSAGE";
    public static final String C_SHOW_NEW_CHAT_NOTIFICATION = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.SHOW_NEW_CHAT_NOTIFICATION";
    public static final String C_CIPHER_SESSION_SET = "com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver.CIPHER_SESSION_SET";


    public static final String C_CHAT_ID_PROP_NAME = "chatId";
    public static final String C_INITIALIZER_PEER_ID_PROP_NAME = "initializerPeerId";
    public static final String C_MESSAGE_TEXT_PROP_NAME = "messageText";
    public static final String C_CHAT_NOTIFICATION_TEXT_PROP_NAME = "chatNotificationText";

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
            case C_CIPHER_SESSION_SET:
                processingError = processCipherSessionSet(intent); break;
            case C_SEND_NEW_MESSAGE:
                processingError = processSendNewMessage(intent); break;
            case C_SHOW_NEW_CHAT_NOTIFICATION:
                processingError = processShowChatNotificationRequest(intent); break;
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

    private Error processCipherSessionSet(
            final Intent data)
    {
        if (data == null)
            return new Error("Intent data was null!", true);

        long chatId = data.getLongExtra(C_CHAT_ID_PROP_NAME, 0);

        if  (chatId == 0)
            return new Error("Cipher Set Data was incorrect!", true);

        m_callback.onCipherSessionSet(chatId);

        return null;
    }

    private Error processSendNewMessage(
            final Intent data)
    {
        if (data == null)
            return new Error("Intent data was null!", true);

        long chatId = data.getLongExtra(C_CHAT_ID_PROP_NAME, 0);
        String text = data.getStringExtra(C_MESSAGE_TEXT_PROP_NAME);

        if  (chatId == 0 || text == null)
            return new Error("Message Data to send was incorrect!", true);

        m_callback.onNewMessageSendingRequested(chatId, text);

        return null;
    }

    private Error processShowChatNotificationRequest(
            final Intent data)
    {
        if (data == null)
            return new Error("Intent data was null!", true);

        String chatNotificationText = data.getStringExtra(C_CHAT_NOTIFICATION_TEXT_PROP_NAME);

        if  (chatNotificationText == null)
            return new Error("Chat Notification Data to show was incorrect!", true);

        m_callback.onNewChatNotificationShowingRequested(chatNotificationText);

        return null;
    }
}
