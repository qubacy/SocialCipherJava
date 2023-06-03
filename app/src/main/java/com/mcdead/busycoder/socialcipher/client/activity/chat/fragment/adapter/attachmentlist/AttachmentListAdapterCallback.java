package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.attachmentlist;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;

public interface AttachmentListAdapterCallback {
    public void onAttachmentClicked(final AttachmentData attachmentData);
    public AttachmentData getAttachmentByIndex(final int index);
    public int getAttachmentListSize();
}
