package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ChatLoadingCallback {
    public void onDialogLoaded();
    public void onDialogLoadingError(final Error error);
}
