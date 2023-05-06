package com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.data.CacheCleanerResult;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

import java.io.File;

public class CacheCleanerStandard extends CacheCleanerBase {
    protected CacheCleanerStandard(
            final CacheCleanerCallback callback)
    {
        super(callback);
    }

    @Override
    protected Pair<Error, Boolean> doInBackground(final Void... voids) {
        // todo: actual cache cleaning..

        SettingsSystem settingsSystem = SettingsSystem.getInstance();

        if (settingsSystem == null)
            return new Pair<>(new Error("System's Settings weren't initialized!", true), false);

        File cacheDir = settingsSystem.getCacheDir();

        if (cacheDir == null)
            return new Pair<>(new Error("Cache Dir was null!", true), false);

        if (!deleteFile(cacheDir))
            return new Pair<>(null, false);

        return new Pair<>(null, true);
    }

    @Override
    protected void onPostExecute(final Pair<Error, Boolean> errorSuccessResult) {
        if (errorSuccessResult.first != null)
            m_callback.onCacheCleanerErrorOccurred(errorSuccessResult.first);
        else
            m_callback.onCacheCleanerResultGotten(new CacheCleanerResult(errorSuccessResult.second));
    }

    private boolean deleteFile(final File file) {
        if (file == null) return false;

        File[] childFileArray = file.listFiles();

        if (file.isDirectory() && (childFileArray != null)) {
            for (final File curFile : childFileArray) {
                if (!deleteFile(curFile)) return false;
            }
        }

        return file.delete();
    }
}
