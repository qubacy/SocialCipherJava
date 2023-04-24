package com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader;

import android.content.ContentResolver;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResult;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public abstract class AttachmentUploaderSyncBase {
    protected String m_token = null;
    protected long m_peerId = 0;

    protected ContentResolver m_contentResolver = null;

    public AttachmentUploaderSyncBase(
            final String token,
            final long peerId,
            final ContentResolver contentResolver)
    {
        m_token = token;
        m_peerId = peerId;

        m_contentResolver = contentResolver;
    }

    public abstract Error uploadAttachments(
            final Object apiObject,
            final List<AttachmentData> uploadingAttachmentList,
            ObjectWrapper<AttachmentUploadedResult> resultAttachmentListStringWrapper);
}
