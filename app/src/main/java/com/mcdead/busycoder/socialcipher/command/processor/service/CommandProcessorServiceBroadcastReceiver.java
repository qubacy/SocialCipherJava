package com.mcdead.busycoder.socialcipher.command.processor.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

/*

    It should get all the COMMANDS and RESPONSES from FRONT-END
    and convey them to the SERVICE;

*/
public class CommandProcessorServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String C_PROVIDE_REQUEST_ANSWER = "com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorServiceBroadcastReceiver.PROVIDE_REQUEST_ANSWER";

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
            case C_PROVIDE_REQUEST_ANSWER:
                processingError = processProvideRequestAnswer(intent); break;
        }

        if (processingError != null)
            m_callback.onServiceBroadcastReceiverErrorOccurred(processingError);
    }

    private Error processProvideRequestAnswer(
            final Intent data)
    {
        if (data == null)
            return new Error(, true);

        m_callback.onDataReceived(data);

        return null;
    }
}
