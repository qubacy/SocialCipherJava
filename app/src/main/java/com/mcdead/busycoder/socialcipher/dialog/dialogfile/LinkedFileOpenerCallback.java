package com.mcdead.busycoder.socialcipher.dialog.dialogfile;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface LinkedFileOpenerCallback {
    public void onFileOpeningFail(Uri fileUri);
    public void onFileOpeningError(Error error);
}
