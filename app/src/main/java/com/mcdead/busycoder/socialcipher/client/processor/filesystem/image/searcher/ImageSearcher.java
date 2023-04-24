package com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
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

    private Error getImages(ObjectWrapper<List<AttachmentData>> resultImageAttachmentDataList) {
        Uri collection = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DISPLAY_NAME
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
        int imageContentTypeColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
        int imageDisplayNameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

        List<AttachmentData> imageAttachmentDataList = new ArrayList<>();

        while (cursor.moveToNext()) {
            long imageId = cursor.getLong(imageIdColumnIndex);
            String imageContentType = cursor.getString(imageContentTypeColumnIndex);
            String imageDisplayName = cursor.getString(imageDisplayNameColumnIndex);
            Uri imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);

            imageAttachmentDataList.add(new AttachmentData(
                    AttachmentType.IMAGE,
                    imageContentType,
                    imageDisplayName,
                    imageUri));
        }

        cursor.close();
        resultImageAttachmentDataList.setValue(imageAttachmentDataList);

        return null;
    }

    @Override
    protected ImageSearcherResult doInBackground(Void... voids) {
        ImageSearcherResult result = new ImageSearcherResult();

        ObjectWrapper<List<AttachmentData>> resultImageAttachmentDataList = new ObjectWrapper<>();
        Error error = getImages(resultImageAttachmentDataList);

        if (error != null) {
            result.error = error;

            return result;
        }

        result.imageAttachmentDataList = resultImageAttachmentDataList.getValue();

        return result;
    }

    @Override
    protected void onPostExecute(ImageSearcherResult imageSearcherResult) {
        if (imageSearcherResult.error != null)
            m_callback.onImageSearcherErrorOccurred(imageSearcherResult.error);
        else
            m_callback.onImageSearcherImagesFound(imageSearcherResult.imageAttachmentDataList);
    }
}
