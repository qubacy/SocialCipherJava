package com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatBroadcastReceiverCallback {
    public void onNewChatMessageReceived();
    public void onChatBroadcastReceiverErrorOccurred(final Error error);
    public void onSettingCipherSessionAnswerRequested(
            final long chatId,
            final long initializePeerId);
    public void onCipherSessionSet(
            final long chatId);
    public void onNewMessageSendingRequested(
            final long chatId,
            final String messageText);
    public void onNewChatNotificationShowingRequested(
            final String chatNotificationText);
}
