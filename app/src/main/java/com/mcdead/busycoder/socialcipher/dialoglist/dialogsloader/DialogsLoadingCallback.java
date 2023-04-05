package com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface DialogsLoadingCallback {
    public void onDialogsLoaded();
    public void onDialogsLoadingError(final Error error);
}
