package com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.util.List;

public interface ImageSearcherCallback {
    public void onImageSearcherErrorOccurred(final Error error);
    public void onImageSearcherImagesFound(final List<AttachmentData> imageAttachmentDataList);
}
