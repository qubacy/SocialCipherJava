package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist;

import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;

public interface MessageListItemCallback {
    public void onAttachmentsShowClicked(final MessageEntity message);
}
