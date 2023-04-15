package com.mcdead.busycoder.socialcipher.attachmentpicker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AttachmentPickerActivityContract extends ActivityResultContract<Void, List<Uri>> {
    public static final int C_RESULT_CODE_OK_VALUE = 0;

    public static final String C_FILE_URI_LIST_WRAPPER_PROP_NAME = "fileUriListWrapper";
    public static final String C_FILE_URI_LIST_PROP_NAME = "fileUriList";

    @NonNull
    @Override
    public Intent createIntent(
            @NonNull Context context,
            Void inputData)
    {
        Intent intent = new Intent(context, AttachmentPickerActivity.class);

        return intent;
    }

    @Override
    public List<Uri> parseResult(
            int resultCode,
            @Nullable Intent intent)
    {
        if (resultCode != C_RESULT_CODE_OK_VALUE || intent == null)
            return null;

        Bundle bundle = intent.getBundleExtra(C_FILE_URI_LIST_WRAPPER_PROP_NAME);

        if (bundle == null) return null;

        List<Uri> fileUriList = (List<Uri>) bundle.getSerializable(C_FILE_URI_LIST_PROP_NAME);

        if (fileUriList == null) return null;

        return fileUriList;
    }
}
