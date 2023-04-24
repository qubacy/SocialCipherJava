package com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ErrorReceivedInterface {
    public void processError(final Error error);
}
