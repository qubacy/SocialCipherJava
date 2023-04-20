package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.doc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;

import androidx.core.content.FileProvider;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.FilesUtility;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.File;

public class LinkedFileOpenerAsync extends AsyncTask<Void, Void, LinkedFileOpenerAsync.AdditionalResult> {
    private Uri m_uri = null;
    private Context m_context = null;
    private LinkedFileOpenerCallback m_callback = null;

    public LinkedFileOpenerAsync(
            final Uri uri,
            Context context,
            LinkedFileOpenerCallback callback)
    {
        m_uri = uri;
        m_context = context;
        m_callback = callback;
    }

    @Override
    protected AdditionalResult doInBackground(Void... voids) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        AdditionalResult additionalResult = new AdditionalResult();

        if (m_uri.getPath().isEmpty()) {
            additionalResult.error = new Error(
                    "Uri was equal to null!",
                    true
            );

            return additionalResult;
        }

        // todo: copying the file..

        ObjectWrapper<Uri> cachedFileUriWrapper = new ObjectWrapper<>();
        Error copyError = copyFileToCache(m_uri, cachedFileUriWrapper);

        if (copyError != null) {
            additionalResult.error = copyError;

            return additionalResult;
        }

        // todo: opening it via Intent..

        if (!openFileViaIntent(cachedFileUriWrapper.getValue()))
            return additionalResult;

        return null;
    }

    @Override
    protected void onPostExecute(AdditionalResult additionalResult) {
        if (additionalResult != null) {
            if (additionalResult.error == null)
                m_callback.onFileOpeningFail(m_uri);
            else
                m_callback.onFileOpeningError(additionalResult.error);
        }
    }

    private boolean openFileViaIntent(
            final Uri fileUri)
    {
        Intent intent = FileContentViewIntentGenerator
                .generateIntentByFileUri(generateAuthorityPathForFile(fileUri));

        if (intent == null) return false;

        m_context.startActivity(intent);

        return true;
    }

    private Error copyFileToCache(
            final Uri uri,
            ObjectWrapper<Uri> resultUri)
    {
        File cacheDir = m_context.getExternalCacheDir();

        return FilesUtility.copyFileTo(
                uri,
                Uri.fromFile(cacheDir),
                resultUri);
    }

    private Uri generateAuthorityPathForFile(final Uri fileUri) {
        return FileProvider.getUriForFile(
                m_context.getApplicationContext(),
                m_context.getApplicationContext().getPackageName() + '.' + "provider",
                new File(fileUri.getPath()));
    }

    public static class AdditionalResult {
        public Error error = null;
    }
}