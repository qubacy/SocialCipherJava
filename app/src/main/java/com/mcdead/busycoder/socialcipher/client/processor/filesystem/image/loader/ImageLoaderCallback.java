package com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.loader;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

public interface ImageLoaderCallback {
    public void onImagesLoaded(final Uri imageUri);
    public void onImagesLoadingError(final Error error);
}
