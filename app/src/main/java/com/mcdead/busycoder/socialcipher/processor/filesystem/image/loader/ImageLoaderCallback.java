package com.mcdead.busycoder.socialcipher.processor.filesystem.image.loader;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

import java.util.List;

public interface ImageLoaderCallback {
    public void onImagesLoaded(final Uri imageUri);
    public void onImagesLoadingError(final Error error);
}
