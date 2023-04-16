package com.mcdead.busycoder.socialcipher.attachmentpicker;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;

import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.dialog.AttachmentPickerCallback;

import java.util.List;

public class AttachmentPickerActivityCallback implements ActivityResultCallback<List<AttachmentData>> {
    private AttachmentPickerCallback m_callback = null;

    public AttachmentPickerActivityCallback(
            AttachmentPickerCallback callback)
    {
        m_callback = callback;
    }

    @Override
    public void onActivityResult(
            final List<AttachmentData> result)
    {
        m_callback.onAttachmentFilesPicked(result);
    }
}
