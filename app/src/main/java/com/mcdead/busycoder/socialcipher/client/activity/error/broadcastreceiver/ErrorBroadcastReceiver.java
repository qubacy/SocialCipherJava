package com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public class ErrorBroadcastReceiver extends BroadcastReceiver {
    public static final String C_ERROR_RECEIVED = "com.mcdead.busycoder.okapitry.error.ErrorBroadcastReceiver.ERROR_RECEIVED";

    public static final String C_ERROR_EXTRA_PROP_NAME = "error";

    private ErrorReceivedInterface m_callback = null;

    public static void broadcastError(final Error error,
                                      Context context)
    {
        Intent intent = new Intent(ErrorBroadcastReceiver.C_ERROR_RECEIVED);

        intent.putExtra(
                ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME,
                error);

        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(intent);
    }

    public ErrorBroadcastReceiver(ErrorReceivedInterface callback) {
        super();

        m_callback = callback;
    }

    @Override
    public void onReceive(Context context,
                          Intent intent)
    {
        if (intent.getAction() != C_ERROR_RECEIVED) return;

        Error error = (Error) intent.getSerializableExtra(C_ERROR_EXTRA_PROP_NAME);

        if (error != null)
            processError(error);
    }

    private void processError(final Error error)
    {
        m_callback.processError(error);
    }
}
