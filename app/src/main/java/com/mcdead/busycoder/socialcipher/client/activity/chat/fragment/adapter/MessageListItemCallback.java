package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter;

import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;

public interface MessageListItemCallback {
    //public void onLinkedAttachmentClicked(final Uri uri);
    public void onAttachmentsShowClicked(final MessageEntity message);
}
