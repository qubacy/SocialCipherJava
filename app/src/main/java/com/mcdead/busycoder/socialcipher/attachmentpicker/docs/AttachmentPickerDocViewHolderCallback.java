package com.mcdead.busycoder.socialcipher.attachmentpicker.docs;

import com.mcdead.busycoder.socialcipher.error.Error;

public interface AttachmentPickerDocViewHolderCallback {
    public void onDocViewHolderDocClicked(final int chosenDocId);
    public void onDocViewHolderErrorOccurred(final Error error);
}
