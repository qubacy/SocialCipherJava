package com.mcdead.busycoder.socialcipher.dialog;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface DialogUpdatedCallback {
    public void onDialogUpdated();
    public void onDialogUpdatingError(final Error error);
}
