package com.mcdead.busycoder.socialcipher.processor.filesystem.image.searcher;

import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

import java.util.List;

public interface ImageSearcherCallback {
    public void onImageSearcherErrorOccurred(final Error error);
    public void onImageSearcherImagesFound(final List<AttachmentData> imageAttachmentDataList);
}
