package com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.util.List;

public class ImageSearcherResult {
    public Error error = null;
    public List<AttachmentData> imageAttachmentDataList = null;
}
