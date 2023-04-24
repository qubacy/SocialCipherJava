package com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatUpdatedCallback {
    public void onNewDialogMessageReceived();
    public void onDialogUpdatingError(final Error error);
}
