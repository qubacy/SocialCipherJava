package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.adapter;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface AttachmentListAdapterCallback {
    public void onAttachmentListError(final Error error);
    public boolean onAttachmentChosen(
            final AttachmentEntityBase attachment,
            final int position);
    public AttachmentEntityBase getAttachmentByIndex(final int index);
    public int getAttachmentListSize();
    public int getLastChosenAttachment();
}
