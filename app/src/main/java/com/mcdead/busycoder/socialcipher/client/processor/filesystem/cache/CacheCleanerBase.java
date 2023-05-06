package com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache;

import android.os.AsyncTask;
import android.util.Pair;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public abstract class CacheCleanerBase extends AsyncTask<Void, Void, Pair<Error, Boolean>> {
    final protected CacheCleanerCallback m_callback;

    protected CacheCleanerBase(final CacheCleanerCallback callback) {
        m_callback = callback;
    }
}
