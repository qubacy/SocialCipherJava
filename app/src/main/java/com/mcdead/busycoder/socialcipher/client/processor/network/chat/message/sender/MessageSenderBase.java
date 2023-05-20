package com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;

import java.util.List;

public abstract class MessageSenderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected long m_peerId = 0;
    protected String m_text = null;
    protected List<AttachmentData> m_uploadingAttachmentList = null;

    protected AttachmentUploaderSyncBase m_attachmentUploader = null;
    protected MessageSendingCallback m_callback = null;

    protected MessageSenderBase(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSendingCallback callback)
    {
        m_token = token;
        m_peerId = peerId;
        m_text = text;
        m_uploadingAttachmentList = uploadingAttachmentList;

        m_attachmentUploader = attachmentUploader;
        m_callback = callback;
    }
}
