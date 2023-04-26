package com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver;

import static com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService.C_OPERATION_ID_PROP_NAME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.CommandSendingCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.NewMessageReceivedCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSendingCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderFactory;

public class ChatListBroadcastReceiver extends BroadcastReceiver
    implements MessageSendingCallback
{
    public static final String C_NEW_MESSAGE_ADDED = "com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.NEW_MESSAGES_ADDED";
    public static final String C_UPDATES_RECEIVED = "com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.UPDATES_RECEIVED";
    public static final String C_SEND_COMMAND_MESSAGE = "com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver.SEND_COMMAND_MESSAGE";

    public static final String C_UPDATES_WRAPPER_EXTRA_PROP_NAME = "updatesWrapper";
    public static final String C_UPDATES_LIST_EXTRA_PROP_NAME = "updatesList";

    public static final String C_NEW_MESSAGE_CHAT_ID_PROP_NAME = "chatId";

    public static final String C_SEND_COMMAND_CHAT_ID_PROP_NAME = "chatId";
    public static final String C_SEND_COMMAND_TEXT_PROP_NAME = "commandText";

    private NewMessageReceivedCallback m_callback = null;
    private CommandSendingCallback m_commandSendingCallback = null;

    public ChatListBroadcastReceiver(
            final NewMessageReceivedCallback callback,
            final CommandSendingCallback commandSendingCallback)
    {
        super();

        m_callback = callback;
        m_commandSendingCallback = commandSendingCallback;
    }

    @Override
    public void onReceive(Context context,
                          Intent intent)
    {
        switch (intent.getAction()) {
            case C_UPDATES_RECEIVED: processUpdatesReceivedAction(context, intent); break;
            case C_NEW_MESSAGE_ADDED: processNewMessagesAddedAction(intent); break;
            case C_SEND_COMMAND_MESSAGE: processSendCommandMessage(intent); break;
        }
    }

    private Error processSendCommandMessage(
            Intent intent)
    {
        long chatId = intent.getLongExtra(C_SEND_COMMAND_CHAT_ID_PROP_NAME, 0);
        String commandText = intent.getStringExtra(C_SEND_COMMAND_TEXT_PROP_NAME);

        if (chatId == 0 || commandText == null)
            return new Error("Incorrect command text has been provided!", true);
        if (commandText.isEmpty())
            return new Error("Incorrect command text has been provided!", true);

        MessageSenderBase messageSender =
                MessageSenderFactory.generateMessageSender(
                        chatId,
                        commandText,
                        null,
                        null,
                        null);

        messageSender.execute();

        return null;
    }

    private void processUpdatesReceivedAction(
            Context context,
            Intent intent)
    {
        Intent serviceIntent = new Intent(
                context.getApplicationContext(),
                UpdateProcessorService.class);

        serviceIntent.putExtras(intent);
        serviceIntent.putExtra(
                C_OPERATION_ID_PROP_NAME,
                UpdateProcessorService.OperationType.PROCESS_RECEIVED_UPDATES.getId());

        context.getApplicationContext().startService(serviceIntent);
    }

    private void processNewMessagesAddedAction(
            Intent intent)
    {
        long chatId = intent.getLongExtra(C_NEW_MESSAGE_CHAT_ID_PROP_NAME, 0);

        if (chatId != 0)
            m_callback.onNewMessageReceived(chatId);
        else
            m_callback.onNewMessageReceivingError(
                    new Error("No Chat Id was provided!", true)
            );
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
