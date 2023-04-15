package com.mcdead.busycoder.socialcipher.attachmentpicker.images;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class ImageSearcher extends AsyncTask<Void, Void, ImageSearcherResult> {
    private Context m_context = null;
    private ImageSearcherCallback m_callback = null;

    public ImageSearcher(
            @NonNull final Context context,
            @NonNull final ImageSearcherCallback callback)
    {
        m_context = context;
        m_callback = callback;
    }

    private Error getImages(ObjectWrapper<List<Uri>> resultImageList) {
        Uri collection = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.Images.Media._ID
        };

        Cursor cursor = m_context.getContentResolver().query(
            collection,
            projection,
            null,
            null,
            MediaStore.Images.Media.DEFAULT_SORT_ORDER
        );

        if (cursor == null)
            return new Error("Images Retrieving process has been failed!", true);

        int imageIdColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);

        List<Uri> imageUriList = new ArrayList<>();

        while (cursor.moveToNext()) {
            long imageId = cursor.getLong(imageIdColumnIndex);
            Uri imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);

            imageUriList.add(imageUri);
        }

        cursor.close();
        resultImageList.setValue(imageUriList);

        return null;
    }

    @Override
    protected ImageSearcherResult doInBackground(Void... voids) {
        ImageSearcherResult result = new ImageSearcherResult();

        ObjectWrapper<List<Uri>> resultImageList = new ObjectWrapper<>();
        Error error = getImages(resultImageList);

        if (error != null) {
            result.error = error;

            return result;
        }

        result.imageUriList = resultImageList.getValue();

        return result;
    }

    @Override
    protected void onPostExecute(ImageSearcherResult imageSearcherResult) {
        if (imageSearcherResult.error != null)
            m_callback.onImageSearcherErrorOccurred(imageSearcherResult.error);
        else
            m_callback.onImageSearcherImagesFound(imageSearcherResult.imageUriList);
    }
}
