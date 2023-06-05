package com.mcdead.busycoder.socialcipher.ui.chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.attachmentlist.AttachmentListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist.MessageListAdapter;

public class ChatFragmentFactory extends FragmentFactory {
    private long m_chatId;
    private long m_localPeerId;
    private ChatFragmentCallback m_callback;

    private com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderBase m_chatLoader;
    private com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase m_attachmentUploader;
    private com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase m_messageSender;

    private ChatBroadcastReceiver m_broadcastReceiver;

    private MessageListAdapter m_messageListAdapter;
    private AttachmentListAdapter m_attachmentListAdapter;

    public ChatFragmentFactory(
            final long chatId,
            final long localPeerId,
            final ChatFragmentCallback callback,
            final com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderBase chatLoader,
            final com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase attachmentUploader,
            final com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase messageSender,
            final ChatBroadcastReceiver chatBroadcastReceiver,
            final MessageListAdapter messageListAdapter,
            final AttachmentListAdapter attachmentListAdapter)
    {
        super();

        m_chatId = chatId;
        m_localPeerId = localPeerId;
        m_callback = callback;

        m_chatLoader = chatLoader;
        m_attachmentUploader = attachmentUploader;
        m_messageSender = messageSender;

        m_broadcastReceiver = chatBroadcastReceiver;
        m_messageListAdapter = messageListAdapter;
        m_attachmentListAdapter = attachmentListAdapter;
    }

    @NonNull
    @Override
    public Fragment instantiate(
            @NonNull ClassLoader classLoader,
            @NonNull String className)
    {
        if (!className.equals(ChatFragment.class.getName()))
            return super.instantiate(classLoader, className);

        return ChatFragment.getInstance(
                m_chatId, m_localPeerId, m_callback,
                m_chatLoader, m_attachmentUploader, m_messageSender,
                m_broadcastReceiver, m_messageListAdapter, m_attachmentListAdapter);
    }
}
