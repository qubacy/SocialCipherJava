package com.mcdead.busycoder.socialcipher.activity.attachmentpicker.intent;

import androidx.activity.result.ActivityResultCallback;

import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.AttachmentPickerCallback;
import com.mcdead.busycoder.socialcipher.activity.attachmentpicker.data.AttachmentData;

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
