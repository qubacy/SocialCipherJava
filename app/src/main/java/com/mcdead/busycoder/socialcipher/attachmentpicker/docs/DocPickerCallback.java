package com.mcdead.busycoder.socialcipher.attachmentpicker.docs;

import android.net.Uri;

import java.util.List;

public interface DocPickerCallback {
    public void onDocsPicked(final List<Uri> docUriList);
}
