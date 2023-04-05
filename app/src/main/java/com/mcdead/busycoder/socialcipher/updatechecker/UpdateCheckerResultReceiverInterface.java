package com.mcdead.busycoder.socialcipher.updatechecker;

import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;

import java.util.List;

public interface UpdateCheckerResultReceiverInterface {
    public void onUpdatesReceived(final List<ResponseUpdateItem> updateItemList);
    public void onErrorOccurred(final Error error);
}
