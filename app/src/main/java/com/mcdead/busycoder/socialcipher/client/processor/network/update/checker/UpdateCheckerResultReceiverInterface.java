package com.mcdead.busycoder.socialcipher.client.processor.update.checker;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateItem;

import java.util.List;

public interface UpdateCheckerResultReceiverInterface {
    public void onUpdatesReceived(final List<ResponseUpdateItem> updateItemList);
    public void onErrorOccurred(final Error error);
}
