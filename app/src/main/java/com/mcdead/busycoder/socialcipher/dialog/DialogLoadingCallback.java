package com.mcdead.busycoder.socialcipher.dialog;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface DialogLoadingCallback {
    public void onDialogLoaded();
    public void onDialogLoadingError(final Error error);
}
