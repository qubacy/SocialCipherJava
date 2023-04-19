package com.mcdead.busycoder.socialcipher.processor.update.checker;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;

import java.util.List;

public interface UpdateCheckerResultReceiverInterface {
    public void onUpdatesReceived(final List<ResponseUpdateItem> updateItemList);
    public void onErrorOccurred(final Error error);
}
