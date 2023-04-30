package com.mcdead.busycoder.socialcipher.command.processor.service;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;

public interface CommandProcessorServiceBroadcastReceiverCallback {
    public void onCommandMessageReceived(final CommandMessage commandMessage);
    public void onRequestAnswered(final RequestAnswer requestAnswer);
    public void onNewSessionInitializingRequested(final long chatId);
    public void onServiceBroadcastReceiverErrorOccurred(final Error error);
}
