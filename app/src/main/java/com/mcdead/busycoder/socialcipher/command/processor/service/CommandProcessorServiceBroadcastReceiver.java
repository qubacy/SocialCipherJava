package com.mcdead.busycoder.socialcipher.command.processor.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;

/*

    It should get all the COMMANDS and RESPONSES from FRONT-END
    and convey them to the SERVICE;

*/
public class CommandProcessorServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String C_PROCESS_COMMAND_MESSAGE = "com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorServiceBroadcastReceiver.PROCESS_COMMAND_MESSAGE";
    public static final String C_PROVIDE_REQUEST_ANSWER = "com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorServiceBroadcastReceiver.PROVIDE_REQUEST_ANSWER";
    public static final String C_INITIALIZE_NEW_CIPHERING_SESSION = "com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorServiceBroadcastReceiver.INITIALIZE_NEW_CIPHERING_SESSION";

    public static final String C_COMMAND_MESSAGE_PROP_NAME = "commandMessage";

    public static final String C_REQUEST_ANSWER_PROP_NAME = "requestAnswer";

    public static final String C_CHAT_ID_PROP_NAME = "chatId";

    final private CommandProcessorServiceBroadcastReceiverCallback m_callback;

    public CommandProcessorServiceBroadcastReceiver(
            final CommandProcessorServiceBroadcastReceiverCallback callback)
    {
        m_callback = callback;
    }

    @Override
    public void onReceive(
            Context context,
            Intent intent)
    {
        // todo: conveying operation to the service..

        Error processingError = null;

        switch (intent.getAction()) {
            case C_PROCESS_COMMAND_MESSAGE:
                processingError = processCommandMessageProcessingRequest(intent); break;
            case C_PROVIDE_REQUEST_ANSWER:
                processingError = processProvideRequestAnswer(intent); break;
            case C_INITIALIZE_NEW_CIPHERING_SESSION:
                processingError = processInitializeNewCipheringSessionRequest(intent); break;
        }

        if (processingError != null)
            m_callback.onServiceBroadcastReceiverErrorOccurred(processingError);
    }

    private Error processCommandMessageProcessingRequest(
            final Intent data)
    {
        if (data == null)
            return new Error("Command Message Processing Request Data was incorrect!", true);

        CommandMessage commandMessage =
                (CommandMessage) data.getSerializableExtra(C_COMMAND_MESSAGE_PROP_NAME);

        if (commandMessage == null)
            return new Error("Provided Command Message was null!", true);

        m_callback.onCommandMessageReceived(commandMessage);

        return null;
    }

    private Error processProvideRequestAnswer(
            final Intent data)
    {
        if (data == null)
            return new Error("Provided Request Answer Data was incorrect!", true);

        RequestAnswer requestAnswer =
                (RequestAnswer) data.getSerializableExtra(C_REQUEST_ANSWER_PROP_NAME);

        if (requestAnswer == null)
            return new Error("Bad Request Answer has been provided!", true);

        m_callback.onRequestAnswered(requestAnswer);

        return null;
    }

    private Error processInitializeNewCipheringSessionRequest(
            final Intent data)
    {
        if (data == null)
            return new Error("Initialize New Ciphering Session Request Data was incorrect!", true);

        long chatId = data.getLongExtra(C_CHAT_ID_PROP_NAME, 0);

        if (chatId == 0)
            return new Error("Bad New Cipher Session init. request data has been provided!", true);

        m_callback.onNewSessionInitializingRequested(chatId);

        return null;
    }
}
