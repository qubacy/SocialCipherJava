package com.mcdead.busycoder.socialcipher.processor.chat.list.loader;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface ChatListLoadingCallback {
    public void onDialogsLoaded();
    public void onDialogsLoadingError(final Error error);
}
