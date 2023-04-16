package com.mcdead.busycoder.socialcipher.dialog;

import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;

import java.util.List;

public interface AttachmentPickerCallback {
    public void onAttachmentFilesPicked(final List<AttachmentData> pickedFileUriList);
}
