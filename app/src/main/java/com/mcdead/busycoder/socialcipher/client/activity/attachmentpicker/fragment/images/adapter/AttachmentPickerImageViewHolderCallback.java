package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.images.adapter;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface AttachmentPickerImageViewHolderCallback {
    public void onImageClicked(final int id);
    public void onViewHolderErrorOccurred(final Error error);
}
