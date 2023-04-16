package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.error.Error;

import java.util.List;

public abstract class MessageSenderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected long m_peerId = 0;
    protected String m_text = null;
    protected List<AttachmentData> m_uploadingAttachmentList = null;

    protected MessageSendingCallback m_callback = null;
    protected ContentResolver m_contentResolver = null;

    public MessageSenderBase(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final MessageSendingCallback callback,
            final ContentResolver contentResolver)
    {
        m_token = token;
        m_peerId = peerId;
        m_text = text;
        m_uploadingAttachmentList = uploadingAttachmentList;

        m_callback = callback;
        m_contentResolver = contentResolver;
    }
}
