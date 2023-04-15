package com.mcdead.busycoder.socialcipher.attachmentpicker.images;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.error.Error;

import java.util.List;

public interface ImageSearcherCallback {
    public void onImageSearcherErrorOccurred(final Error error);
    public void onImageSearcherImagesFound(final List<Uri> imageUriList);
}
