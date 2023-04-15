package com.mcdead.busycoder.socialcipher.dialog;

import android.net.Uri;

import java.util.List;

public interface AttachmentPickerCallback {
    public void onAttachmentFilesPicked(final List<Uri> pickedFileUriList);
}
