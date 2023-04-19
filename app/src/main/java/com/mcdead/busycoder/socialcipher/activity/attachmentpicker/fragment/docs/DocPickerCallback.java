package com.mcdead.busycoder.socialcipher.activity.attachmentpicker.fragment.docs;

import android.net.Uri;

import java.util.List;

public interface DocPickerCallback {
    public void onDocsPicked(final List<Uri> docUriList);
}
