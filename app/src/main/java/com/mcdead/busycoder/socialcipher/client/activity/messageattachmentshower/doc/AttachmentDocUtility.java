package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class AttachmentDocUtility {
    public static void showFileShowingFailedToast(
            final Context context,
            final Uri fileUri)
    {
        Toast.makeText(
                context,
                "File is available here: " + fileUri.getPath(),
                Toast.LENGTH_LONG).show();
    }
}
