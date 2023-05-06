package com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.data.CacheCleanerResult;

public interface CacheCleanerCallback {
    public void onCacheCleanerErrorOccurred(final Error error);
    public void onCacheCleanerResultGotten(final CacheCleanerResult cacheCleanerResult);
}
