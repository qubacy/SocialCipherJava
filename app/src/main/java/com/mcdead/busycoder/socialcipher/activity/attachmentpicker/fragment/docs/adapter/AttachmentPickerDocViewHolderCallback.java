package com.mcdead.busycoder.socialcipher.activity.attachmentpicker.fragment.docs.adapter;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

public interface AttachmentPickerDocViewHolderCallback {
    public void onDocViewHolderDocClicked(final int chosenDocId);
    public void onDocViewHolderErrorOccurred(final Error error);
}
