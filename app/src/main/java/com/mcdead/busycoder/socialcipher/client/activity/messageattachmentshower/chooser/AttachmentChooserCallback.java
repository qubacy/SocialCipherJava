package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.chooser;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

public interface AttachmentChooserCallback {
    public void onAttachmentChosen(final AttachmentEntityBase chosenAttachment);
}
