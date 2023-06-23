package com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatListLoadingCallback {
    public void onChatListLoaded();
    public void onChatListLoadingError(final Error error);
}
