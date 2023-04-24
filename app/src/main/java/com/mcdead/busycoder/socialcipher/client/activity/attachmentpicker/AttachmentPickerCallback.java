package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;

import java.util.List;

public interface AttachmentPickerCallback {
    public void onAttachmentFilesPicked(final List<AttachmentData> pickedFileUriList);
}
