package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;

public interface MessageListAdapterCallback extends MessageListItemCallback {
    public MessageEntity getMessageByIndex(final int index);
    public int getMessageListSize();
    public void onErrorOccurred(final Error error);
}
