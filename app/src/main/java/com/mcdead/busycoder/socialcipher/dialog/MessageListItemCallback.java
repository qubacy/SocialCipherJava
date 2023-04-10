package com.mcdead.busycoder.socialcipher.dialog;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

public interface MessageListItemCallback {
    //public void onLinkedAttachmentClicked(final Uri uri);
    public void onAttachmentsShowClicked(final MessageEntity message);
}
