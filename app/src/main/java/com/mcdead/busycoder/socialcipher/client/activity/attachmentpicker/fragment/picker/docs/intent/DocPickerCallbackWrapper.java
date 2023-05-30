package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.intent;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.DocPickerCallback;

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
