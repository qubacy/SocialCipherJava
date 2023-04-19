package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;

public interface AttachmentChooserCallback {
    public void onAttachmentChosen(final AttachmentEntityBase chosenAttachment);
}
