package com.mcdead.busycoder.socialcipher.processor.filesystem.docsearcher;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.fragment.docs.data.DocData;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class DocSearcher extends AsyncTask<Void, Void, DocSearcherResult> {
    //private String m_dirPath = null;
    private Context m_context = null;

    private DocSearcherCallback m_callback = null;

    public DocSearcher(
            //@NonNull String dirPath,
            @NonNull Context context,
            @NonNull DocSearcherCallback callback)
    {
        //m_dirPath = dirPath;
        m_context = context;
        m_callback = callback;
    }

    private Error getDocs(ObjectWrapper<List<DocData>> resultDocsList) {
        Uri collection = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else
            collection = Uri.parse("external");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
//        else
//            collection = MediaStore.Files. //.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?";
//        String[] selectionArgs = new String[] {
//                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT)
//        };

        Cursor cursor = m_context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        );

        if (cursor == null)
            return new Error("Docs Retrieving process has been failed!", true);

        int docIdColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int docDisplayNameColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
        int docMimeTypeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);

        List<DocData> docUriList = new ArrayList<>();

        while (cursor.moveToNext()) {
            long docId = cursor.getLong(docIdColumnIndex);
            String docDisplayName = cursor.getString(docDisplayNameColumnIndex);
            String docMimeType = cursor.getString(docMimeTypeColumnIndex);

            Uri docUri = MediaStore.Files.getContentUri(
                    MediaStore.VOLUME_EXTERNAL, docId);

            docUriList.add(new DocData(docUri, docDisplayName));
        }

        cursor.close();
        resultDocsList.setValue(docUriList);

//        File filesDir = new File(m_dirPath);
//
//        if (filesDir == null)
//            return new Error("No External Storage Dir has been provided!", true);
//        if (!filesDir.exists())
//            return new Error("Provided External Storage Dir doesn't exist!", true);
//
//        File[] files = filesDir.listFiles();
//        List<Uri> fileUriList = new ArrayList<>();
//
//        if (files == null) {
//            resultDocsList.setValue(fileUriList);
//
//            return null;
//        }
//
//        for (final File file : files) {
//            Uri fileUri = Uri.fromFile(file);
//
//            if (fileUri == null)
//                return new Error("Uri creation process has been gone wrong!", true);
//
//            fileUriList.add(fileUri);
//        }
//
//        resultDocsList.setValue(fileUriList);
//
        return null;
    }

    @Override
    protected DocSearcherResult doInBackground(Void... voids) {
        DocSearcherResult result = new DocSearcherResult();

        ObjectWrapper<List<DocData>> resultDocList = new ObjectWrapper<>();
        Error error = getDocs(resultDocList);

        if (error != null) {
            result.error = error;

            return result;
        }

        result.docUriList = resultDocList.getValue();

        return result;
    }

    @Override
    protected void onPostExecute(
            final DocSearcherResult docSearcherResult)
    {
        if (docSearcherResult.error != null)
            m_callback.onDocSearcherErrorOccurred(docSearcherResult.error);
        else
            m_callback.onDocSearcherDocsFound(docSearcherResult.docUriList);
    }
}
