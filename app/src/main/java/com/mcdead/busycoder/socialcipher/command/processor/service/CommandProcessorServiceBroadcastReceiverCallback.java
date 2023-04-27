package com.mcdead.busycoder.socialcipher.command.processor.service;

import android.content.Intent;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface CommandProcessorServiceBroadcastReceiverCallback {
    public void onDataReceived(final Intent intent);
    public void onServiceBroadcastReceiverErrorOccurred(final Error error);
}
