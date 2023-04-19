package com.mcdead.busycoder.socialcipher.activity.chat.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChatBroadcastReceiver extends BroadcastReceiver {
    public static final String C_NEW_MESSAGE_ADDED = "com.mcdead.busycoder.okapitry.dialog.DialogBroadcastReceiver.NEW_MESSAGE_ADDED";

    private ChatUpdatedCallback m_callback = null;

    public ChatBroadcastReceiver(final ChatUpdatedCallback callback) {
        super();

        m_callback = callback;
    }

    @Override
    public void onReceive(Context context,
                          Intent intent)
    {
        switch (intent.getAction()) {
            case C_NEW_MESSAGE_ADDED: processNewMessageAddedAction();
        }
    }

    private void processNewMessageAddedAction() {
        m_callback.onNewDialogMessageReceived();
    }
}
