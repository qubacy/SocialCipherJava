package com.mcdead.busycoder.socialcipher.attachmentpicker.docs;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.error.Error;

import java.util.List;

public interface DocSearcherCallback {
    public void onDocSearcherErrorOccurred(final Error error);
    public void onDocSearcherDocsFound(final List<DocData> docUriList);
}
