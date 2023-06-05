package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private Long m_chatId = null;
    private Long m_localPeerId = null;

    private ChatFragmentCallback m_callback = null;

    private ChatLoaderBase m_chatLoader = null;
    private AttachmentUploaderSyncBase m_attachmentUploader = null;
    private MessageSenderBase m_messageSender = null;

    private List<AttachmentData> m_uploadingAttachmentList = null;

    private boolean m_isWaitingForCipherSessionSet = false;

    public ChatViewModel() {
        super();

        m_uploadingAttachmentList = new ArrayList<>();
    }

    public boolean setChatId(final long chatId) {
        if (m_chatId != null) return false;

        m_chatId = chatId;

        return true;
    }

    public boolean setLocalPeerId(final long localPeerId) {
        if (m_localPeerId != null) return false;

        m_localPeerId = localPeerId;

        return true;
    }

    public boolean setCallback(final ChatFragmentCallback callback) {
        if (callback == null || m_callback != null)
            return false;

        m_callback = callback;

        return true;
    }

    public boolean setChatLoader(final ChatLoaderBase chatLoader) {
        if (chatLoader == null || m_chatLoader != null)
            return false;

        m_chatLoader = chatLoader;

        return true;
    }

    public boolean setAttachmentUploader(final AttachmentUploaderSyncBase attachmentUploader) {
        if (attachmentUploader == null || m_attachmentUploader != null)
            return false;

        m_attachmentUploader = attachmentUploader;

        return true;
    }

    public boolean setMessageSender(final MessageSenderBase messageSender) {
        if (messageSender == null || m_messageSender != null)
            return false;

        m_messageSender = messageSender;

        return true;
    }

    public boolean setUploadingAttachmentDataList(
            final List<AttachmentData> uploadingAttachmentDataList)
    {
        m_uploadingAttachmentList = uploadingAttachmentDataList;

        return true;
    }

    public AttachmentData getUploadingAttachmentDataByIndex(final int index) {
        if (index < 0 || index >= m_uploadingAttachmentList.size())
            return null;

        return m_uploadingAttachmentList.get(index);
    }

    public boolean addUploadingAttachmentData(
            final AttachmentData attachmentData)
    {
        if (attachmentData == null) return false;

        for (final AttachmentData curAttachmentData : m_uploadingAttachmentList) {
            if (curAttachmentData.getUri().equals(attachmentData.getUri()))
                return true;
        }

        m_uploadingAttachmentList.add(attachmentData);

        return true;
    }

    public boolean removeUploadingAttachmentData(
            final AttachmentData attachmentData)
    {
        if (attachmentData == null) return false;

        return m_uploadingAttachmentList.remove(attachmentData);
    }

    public boolean setWaitingForCipherSessionSet(final boolean isWaitingForCipherSessionSet) {
        if (isWaitingForCipherSessionSet == m_isWaitingForCipherSessionSet) return false;

        m_isWaitingForCipherSessionSet = isWaitingForCipherSessionSet;

        return true;
    }

    public long getChatId() {
        return m_chatId;
    }

    public long getLocalPeerId() {
        return m_localPeerId;
    }

    public ChatFragmentCallback getCallback() {
        return m_callback;
    }

    public AttachmentUploaderSyncBase getAttachmentUploader() {
        return m_attachmentUploader;
    }

    public MessageSenderBase getMessageSender() {
        return m_messageSender;
    }

    public ChatLoaderBase getChatLoader() {
        return m_chatLoader;
    }

    public List<AttachmentData> getUploadingAttachmentList() {
        return m_uploadingAttachmentList;
    }

    public boolean isWaitingForCipherSessionSet() {
        return m_isWaitingForCipherSessionSet;
    }

    public boolean isInitialized() {
        return (m_chatId != null && m_localPeerId != null && m_callback != null &&
                m_chatLoader != null && m_attachmentUploader != null &&
                m_messageSender != null);
    }
}
