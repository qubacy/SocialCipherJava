package com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.data;

public class CacheCleanerResult {
    final private boolean m_isSuccessful;

    public CacheCleanerResult(
            final boolean isSuccessful)
    {
        m_isSuccessful = isSuccessful;
    }

    public boolean isSuccessful() {
        return m_isSuccessful;
    }
}
