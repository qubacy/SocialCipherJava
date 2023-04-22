package com.mcdead.busycoder.socialcipher.processor.filesystem.image.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;

import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader extends AsyncTask<Void, Void, ImageLoaderResult> {
    public static final String C_CACHE_DIR_NAME = "preview_images";

    private static final int C_THUMBNAIL_SIZE = 256;

    private Uri m_imageToLoadUri = null;

    private Context m_context = null;
    private ImageLoaderCallback m_callback = null;

    public ImageLoader(
            @NonNull final Uri imageToLoadUri,
            @NonNull final Context context,
            @NonNull final ImageLoaderCallback callback)
    {
        m_imageToLoadUri = imageToLoadUri;

        m_context = context;
        m_callback = callback;
    }

    private File getCacheDir() {
        File generalCacheDir = m_context.getExternalCacheDir();

        if (!generalCacheDir.exists() || !generalCacheDir.isDirectory())
            return null;

        File cacheDir = new File(generalCacheDir, C_CACHE_DIR_NAME);

        if (!cacheDir.exists())
            if (!cacheDir.mkdir()) return null;

        if (!cacheDir.isDirectory()) return null;

        return cacheDir;
    }

    private File checkImageInLocalCache(
            final File[] cachedImageList,
            final String imageFileName)
    {
        for (final File cachedImage : cachedImageList) {
            if (imageFileName.compareTo(cachedImage.getName()) == 0)
                return cachedImage;
        }

        return null;
    }

    private Error createImagePreview(
            final Uri imageUri,
            final String imageFileName,
            final File cacheDir,
            final ContentResolver contentResolver,
            ObjectWrapper<Uri> createdImageUri)
    {
        Bitmap thumbnail = null;

        try (InputStream imageStream = contentResolver.openInputStream(imageUri)) {
            thumbnail = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeStream(imageStream),
                    C_THUMBNAIL_SIZE, C_THUMBNAIL_SIZE);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error("New Preview image creation process went wrong!", true);
        }

        File cachedImageFile = new File(cacheDir, imageFileName);

        try {
            if (!cachedImageFile.createNewFile())
                return new Error("New Preview image file creation process went wrong!", true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error("New Preview image file creation process went wrong!", true);
        }

        try (FileOutputStream out = new FileOutputStream(cachedImageFile)) {
            if (!thumbnail.compress(Bitmap.CompressFormat.PNG, 100, out))
                return new Error("New Preview image file writing from buffer process went wrong!", true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error("New Preview image file writing process went wrong!", true);
        }

        createdImageUri.setValue(Uri.fromFile(cachedImageFile));

        return null;
    }

    private String resolveFileNameByUri(
            final Uri uri,
            final ContentResolver contentResolver)
    {
        Cursor fileCursor =
                contentResolver.query(
                        uri,
                        null,
                        null,
                        null,
                        null);

        fileCursor.moveToFirst();

        if (fileCursor.getCount() <= 0) {
            fileCursor.close();

            return null;
        }

        int fileNameColumn = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        String fileName = fileCursor.getString(fileNameColumn);

        fileCursor.close();

        return fileName;
    }

    private Error loadImages(
            ObjectWrapper<Uri> loadedImageUriWrapper)
    {
        ContentResolver contentResolver = m_context.getContentResolver();
        String fileName = resolveFileNameByUri(m_imageToLoadUri, contentResolver);

        if (fileName == null)
            return new Error("Resolving File Name by Uri went wrong!", true);

        File cacheDir = getCacheDir();

        if (cacheDir == null)
            return new Error("Couldn't get a cache dir for loading images!", true);

        File[] cacheDirFileList = cacheDir.listFiles();

        if (cacheDirFileList != null) {
            File cachedImageFile = checkImageInLocalCache(cacheDirFileList, fileName);

            if (cachedImageFile != null) {
                loadedImageUriWrapper.setValue(Uri.fromFile(cachedImageFile));

                return null;
            }
        }

        ObjectWrapper<Uri> createdImageWrapper = new ObjectWrapper<>();
        Error creatingError =
                createImagePreview(
                        m_imageToLoadUri,
                        fileName,
                        cacheDir,
                        contentResolver,
                        createdImageWrapper);

        if (creatingError != null) return creatingError;

        loadedImageUriWrapper.setValue(createdImageWrapper.getValue());

        return null;
    }

    @Override
    protected ImageLoaderResult doInBackground(
            Void... voids)
    {
        ImageLoaderResult imageLoaderResult = new ImageLoaderResult();

        ObjectWrapper<Uri> loadedImageUriWrapper = new ObjectWrapper<>();
        Error loadingError = loadImages(loadedImageUriWrapper);

        if (loadingError != null)
            imageLoaderResult.error = loadingError;
        else
            imageLoaderResult.imageAttachmentUri = loadedImageUriWrapper.getValue();

        return imageLoaderResult;
    }

    @Override
    protected void onPostExecute(
            final ImageLoaderResult imageLoaderResult)
    {
        if (imageLoaderResult.error != null)
            m_callback.onImagesLoadingError(imageLoaderResult.error);
        else
            m_callback.onImagesLoaded(imageLoaderResult.imageAttachmentUri);
    }
}
