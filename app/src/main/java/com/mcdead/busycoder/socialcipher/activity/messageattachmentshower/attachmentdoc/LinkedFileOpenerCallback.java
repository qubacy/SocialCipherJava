package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.attachmentdoc;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface LinkedFileOpenerCallback {
    public void onFileOpeningFail(Uri fileUri);
    public void onFileOpeningError(Error error);
}
