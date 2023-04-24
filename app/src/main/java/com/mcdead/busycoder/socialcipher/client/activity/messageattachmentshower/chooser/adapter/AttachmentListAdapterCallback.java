package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.chooser.adapter;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface AttachmentListAdapterCallback {
    public void onAttachmentListError(final Error error);
    public void onAttachmentChosen(final AttachmentEntityBase attachment);
}
