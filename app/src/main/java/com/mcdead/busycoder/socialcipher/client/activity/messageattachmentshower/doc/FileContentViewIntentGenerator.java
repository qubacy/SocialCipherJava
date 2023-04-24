package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc;

import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FileContentViewIntentGenerator {
    public static Intent generateIntentByFileUri(
            final Uri uri)
    {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        if (mimeType == null) return null;

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(uri, mimeType);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }
}
