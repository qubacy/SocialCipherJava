package com.mcdead.busycoder.socialcipher.attachmentpicker.docs;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;

import java.util.List;

public class DocPickerCallbackWrapper implements ActivityResultCallback<List<Uri>> {
    private DocPickerCallback m_callback = null;

    public DocPickerCallbackWrapper(final DocPickerCallback callback) {
        m_callback = callback;
    }

    @Override
    public void onActivityResult(final List<Uri> docUriList) {
        m_callback.onDocsPicked(docUriList);
    }
}
