package com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver;

import static com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService.C_OPERATION_ID_PROP_NAME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.CommandSendingCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.NewMessageReceivedCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.sender.data.MessageToSendData;
import com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSendingCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase;

public class ChatListBroadcastReceiver extends BroadcastReceiver
    implements MessageSendingCallback
{
    public static final String C_NEW_MESSAGE_ADDED =
            "com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.NEW_MESSAGES_ADDED";
    public static final String C_UPDATES_RECEIVED =
            "com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.UPDATES_RECEIVED";
    public static final String C_SEND_COMMAND_MESSAGE =
            "com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.SEND_COMMAND_MESSAGE";

    public static final String C_UPDATES_WRAPPER_EXTRA_PROP_NAME = "updatesWrapper";
    public static final String C_UPDATES_LIST_EXTRA_PROP_NAME = "updatesList";

    public static final String C_NEW_MESSAGE_CHAT_ID_PROP_NAME = "chatId";

    public static final String C_SEND_COMMAND_CHAT_ID_PROP_NAME = "chatId";
    public static final String C_SEND_COMMAND_TEXT_PROP_NAME = "commandText";

    private NewMessageReceivedCallback m_newMessageReceivedCallback = null;
    private CommandSendingCallback m_commandSendingCallback = null;

    final private ChatIdChecker m_chatIdChecker;
    final private MessageSenderBase m_messageSender;

    protected ChatListBroadcastReceiver(
            final NewMessageReceivedCallback callback,
            final CommandSendingCallback commandSendingCallback,
            final ChatIdChecker chatIdChecker,
            final MessageSenderBase messageSender)
    {
        super();

        m_newMessageReceivedCallback = callback;
        m_commandSendingCallback = commandSendingCallback;
        m_chatIdChecker = chatIdChecker;
        m_messageSender = messageSender;
    }

    public static ChatListBroadcastReceiver getInstance(
            final NewMessageReceivedCallback callback,
            final CommandSendingCallback commandSendingCallback,
            final ChatIdChecker chatIdChecker,
            final MessageSenderBase messageSender)
    {
        if (chatIdChecker == null || messageSender == null)
            return null;

        return new ChatListBroadcastReceiver(
                callback, commandSendingCallback, chatIdChecker, messageSender);
    }

    public boolean setCommandSendingCallback(
            final CommandSendingCallback commandSendingCallback)
    {
        if (commandSendingCallback == null)
            return false;

        m_commandSendingCallback = commandSendingCallback;

        return true;
    }

    public boolean setNewMessageReceivedCallback(
            final NewMessageReceivedCallback newMessageReceivedCallback)
    {
        if (newMessageReceivedCallback == null)
            return false;

        m_newMessageReceivedCallback = newMessageReceivedCallback;

        return true;
    }

    @Override
    public void onReceive(
            final Context context,
            final Intent intent)
    {
        Error processingError = processBroadcast(context, intent);

        if (processingError != null)
            m_newMessageReceivedCallback.onNewMessageReceivingError(processingError);
    }

    private Error processBroadcast(
            final Context context,
            final Intent intent)
    {
        switch (intent.getAction()) {
            case C_UPDATES_RECEIVED: return processUpdatesReceivedAction(context, intent);
            case C_NEW_MESSAGE_ADDED: return processNewMessagesAddedAction(intent);
            case C_SEND_COMMAND_MESSAGE: return processSendCommandMessage(intent);
        }

        return null;
    }

    private Error processSendCommandMessage(
            final Intent intent)
    {
        long chatId = intent.getLongExtra(C_SEND_COMMAND_CHAT_ID_PROP_NAME, 0);
        String commandText = intent.getStringExtra(C_SEND_COMMAND_TEXT_PROP_NAME);

        if (chatId == 0 || commandText == null)
            return new Error("Incorrect command text has been provided!", true);
        if (commandText.isEmpty())
            return new Error("Incorrect command text has been provided!", true);

        MessageToSendData messageToSendData =
                MessageToSendData.getInstance(
                        chatId, commandText, null);

        if (messageToSendData == null)
            return new Error("Message To Send can't be generated!", true);

        m_messageSender.execute(messageToSendData);

        return null;
    }

    private Error processUpdatesReceivedAction(
            final Context context,
            final Intent intent)
    {
        Intent serviceIntent = new Intent(
                context.getApplicationContext(),
                UpdateProcessorService.class);

        serviceIntent.putExtras(intent);
        serviceIntent.putExtra(
                C_OPERATION_ID_PROP_NAME,
                UpdateProcessorService.OperationType.PROCESS_RECEIVED_UPDATES.getId());

        context.getApplicationContext().startService(serviceIntent);

        return null;
    }

    private Error processNewMessagesAddedAction(
            final Intent intent)
    {
        long chatId = intent.getLongExtra(C_NEW_MESSAGE_CHAT_ID_PROP_NAME, 0);

        if (!m_chatIdChecker.isValid(chatId))
            return new Error("No Chat Id was provided!", true);

        m_newMessageReceivedCallback.onNewMessageReceived(chatId);

        return null;
    }

    @Override
    public void onMessageSent() {
        // nothing??
    }

    @Override
    public void onMessageSendingError(
            final Error error)
    {
        m_commandSendingCallback.onNewCommandSendingError(error);
    }
}
