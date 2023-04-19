package com.mcdead.busycoder.socialcipher.activity.chat.fragment.adapter;

import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

public interface MessageListItemCallback {
    //public void onLinkedAttachmentClicked(final Uri uri);
    public void onAttachmentsShowClicked(final MessageEntity message);
}
