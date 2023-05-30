package com.mcdead.busycoder.socialcipher.client.processor.filesystem.doc.searcher;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.data.DocData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.util.List;

public class DocSearcherResult {
    public Error error;
    public List<DocData> docUriList = null;
}
