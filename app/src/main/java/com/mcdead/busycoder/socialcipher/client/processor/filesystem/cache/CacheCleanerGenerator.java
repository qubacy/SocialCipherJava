package com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache;

public class CacheCleanerGenerator {
    public static CacheCleanerBase generateCacheCleaner(
            final CacheCleanerCallback callback)
    {
        if (callback == null) return null;

        return new CacheCleanerStandard(callback);
    }
}
