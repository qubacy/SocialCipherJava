package com.mcdead.busycoder.socialcipher.processor.filesystem.docsearcher;

import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.fragment.docs.data.DocData;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

import java.util.List;

public interface DocSearcherCallback {
    public void onDocSearcherErrorOccurred(final Error error);
    public void onDocSearcherDocsFound(final List<DocData> docUriList);
}
