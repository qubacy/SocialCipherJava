package com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result;

public class AttachmentUploadedResultVK implements AttachmentUploadedResult {
    private String m_attachmentListString = null;

    public AttachmentUploadedResultVK(
            final String attachmentListString)
    {
        m_attachmentListString = attachmentListString;
    }

    public String getAttachmentListString() {
        return m_attachmentListString;
    }
}
