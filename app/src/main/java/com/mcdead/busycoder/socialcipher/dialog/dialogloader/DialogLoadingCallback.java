package com.mcdead.busycoder.socialcipher.dialog.dialogloader;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface DialogLoadingCallback {
    public void onDialogLoaded();
    public void onDialogLoadingError(final Error error);
}
