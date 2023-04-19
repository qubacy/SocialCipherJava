package com.mcdead.busycoder.socialcipher.activity.attachmentpicker.fragment.docs.intent;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DocPickerContract extends ActivityResultContract<Void, List<Uri>> {
    //public static final int C_RESULT_CODE_OK_VALUE = 0;

    @NonNull
    @Override
    public Intent createIntent(
            @NonNull Context context,
            Void unused)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        return intent;
    }

    @Override
    public List<Uri> parseResult(
            int resultCode,
            @Nullable Intent intent)
    {
        if (intent == null)
            return null;

        ClipData clipData = intent.getClipData();
        List<Uri> docUriList = new ArrayList<>();

        if (clipData == null) {
            Uri docUri = intent.getData();

            if (docUri == null) return null;

            docUriList.add(docUri);

            return docUriList;
        }

        for(int i = 0; i < clipData.getItemCount(); i++) {
            ClipData.Item path = clipData.getItemAt(i);
            Uri docUri = path.getUri();

            docUriList.add(docUri);
        }

        return docUriList;
    }
}
