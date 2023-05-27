package com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader;

import android.content.ContentResolver;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResult;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public abstract class AttachmentUploaderSyncBase {
    final protected String m_token;

    protected ContentResolver m_contentResolver = null;

    protected AttachmentUploaderSyncBase(
            final String token,
            final ContentResolver contentResolver)
    {
        m_token = token;

        m_contentResolver = contentResolver;
    }

    public boolean setContentResolver(
            final ContentResolver contentResolver)
    {
        if (contentResolver == null || m_contentResolver != null)
            return false;

        m_contentResolver = contentResolver;

        return true;
    }

    public abstract Error uploadAttachments(
            final long chatId,
            final List<AttachmentData> uploadingAttachmentList,
            ObjectWrapper<AttachmentUploadedResult> resultAttachmentListStringWrapper);
}
