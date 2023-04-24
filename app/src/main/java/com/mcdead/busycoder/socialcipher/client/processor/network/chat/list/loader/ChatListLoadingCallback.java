package com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatListLoadingCallback {
    public void onDialogsLoaded();
    public void onDialogsLoadingError(final Error error);
}
