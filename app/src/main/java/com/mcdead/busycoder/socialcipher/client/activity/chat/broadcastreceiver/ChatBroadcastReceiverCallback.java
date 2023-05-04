package com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatBroadcastReceiverCallback {
    public void onNewChatMessageReceived();
    public void onChatBroadcastReceiverErrorOccurred(final Error error);
    public void onSettingCipherSessionAnswerRequested(
            final long chatId,
            final long initializePeerId,
            final long messageId);
    public void onCipherSessionSettingEnded(
            final long chatId,
            final boolean isCipherSessionSet);
    public void onNewMessageSendingRequested(
            final long chatId,
            final String messageText);
    public void onNewChatNotificationShowingRequested(
            final String chatNotificationText);
}
