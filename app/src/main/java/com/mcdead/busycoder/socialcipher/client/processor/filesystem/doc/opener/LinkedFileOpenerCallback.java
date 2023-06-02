package com.mcdead.busycoder.socialcipher.client.processor.filesystem.doc.opener;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface LinkedFileOpenerCallback {
    public void onFileOpeningFail(Uri fileUri);
    public void onFileOpeningError(Error error);
}
