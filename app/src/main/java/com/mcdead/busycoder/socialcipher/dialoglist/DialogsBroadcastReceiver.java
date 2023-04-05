package com.mcdead.busycoder.socialcipher.dialoglist;

import static com.mcdead.busycoder.socialcipher.updateprocessor.UpdateProcessorService.C_OPERATION_ID_PROP_NAME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader.DialogsLoadingCallback;
import com.mcdead.busycoder.socialcipher.updateprocessor.UpdateProcessorService;

public class DialogsBroadcastReceiver extends BroadcastReceiver {
    public static final String C_NEW_MESSAGES_ADDED = "com.mcdead.busycoder.okapitry.dialoglist.DialogsBroadcastReceiver.NEW_MESSAGES_ADDED";
    public static final String C_UPDATES_RECEIVED = "com.mcdead.busycoder.okapitry.dialoglist.DialogsBroadcastReceiver.UPDATES_RECEIVED";

    public static final String C_UPDATES_WRAPPER_EXTRA_PROP_NAME = "updatesWrapper";
    public static final String C_UPDATES_LIST_EXTRA_PROP_NAME = "updatesList";

    private DialogsLoadingCallback m_callback = null;

    public DialogsBroadcastReceiver(final DialogsLoadingCallback callback) {
        super();

        m_callback = callback;
    }

    @Override
    public void onReceive(Context context,
                          Intent intent)
    {
        switch (intent.getAction()) {
            case C_UPDATES_RECEIVED: processUpdatesReceivedAction(context, intent); break;
            case C_NEW_MESSAGES_ADDED: processNewMessagesAddedAction();
        }
    }

    private void processUpdatesReceivedAction(Context context,
                                              Intent intent) {
        Intent serviceIntent = new Intent(
                context.getApplicationContext(),
                UpdateProcessorService.class);


        serviceIntent.putExtras(intent);
        serviceIntent.putExtra(C_OPERATION_ID_PROP_NAME, UpdateProcessorService.OperationType.PROCESS_RECEIVED_UPDATES.getId());

        context.getApplicationContext().startService(serviceIntent);
    }

    private void processNewMessagesAddedAction() {
        m_callback.onDialogsLoaded();
    }
}
