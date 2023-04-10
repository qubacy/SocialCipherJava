package com.mcdead.busycoder.socialcipher.attachmentshower;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.error.Error;

public interface AttachmentListAdapterCallback {
    public void onAttachmentListError(final Error error);
    public void onAttachmentChosen(final AttachmentEntityBase attachment);
}
