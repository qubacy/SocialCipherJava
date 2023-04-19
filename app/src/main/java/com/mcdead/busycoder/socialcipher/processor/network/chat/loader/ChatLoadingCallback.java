package com.mcdead.busycoder.socialcipher.processor.chat.loader;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface ChatLoadingCallback {
    public void onDialogLoaded();
    public void onDialogLoadingError(final Error error);
}
