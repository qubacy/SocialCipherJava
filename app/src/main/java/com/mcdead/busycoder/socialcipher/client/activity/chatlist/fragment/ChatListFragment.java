package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.client.activity.chat.ChatActivity;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter.ChatListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter.ChatListAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter.ChatListItemCallback;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoaderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoaderFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoadingCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncFactory;

import java.util.List;

public class ChatListFragment extends Fragment
    implements ChatListLoadingCallback,
        ChatListAdapterCallback,
        ChatListItemCallback,
        NewMessageReceivedCallback,
        CommandSendingCallback
{
    final private ChatListFragmentCallback m_callback;
    final private ChatListLoaderBase m_chatListLoader;
    final private ChatListBroadcastReceiver m_chatChangeBroadcastReceiver;
    final private ChatListAdapter m_chatListAdapter;

    private ChatListViewModel m_chatListViewModel = null;

    private RecyclerView m_dialogsListRecyclerView = null;

    protected ChatListFragment(
            final ChatListFragmentCallback callback,
            final ChatListLoaderBase chatListLoader,
            final ChatListBroadcastReceiver chatListBroadcastReceiver,
            final ChatListAdapter chatListAdapter)
    {
        super();

        m_callback = callback;
        m_chatListLoader = chatListLoader;
        m_chatChangeBroadcastReceiver = chatListBroadcastReceiver;
        m_chatListAdapter = chatListAdapter;
    }

    public static ChatListFragment getInstance(
            final ChatListFragmentCallback callback,
            final Context context)
    {
        if (callback == null || context == null)
            return null;

        ChatListLoaderBase chatListLoader =
                ChatListLoaderFactory.generateChatListLoader(null);

        if (chatListLoader == null) return null;

        AttachmentUploaderSyncBase attachmentUploader =
                AttachmentUploaderSyncFactory.generateAttachmentUploader(null);

        if (attachmentUploader == null) return null;

        MessageSenderBase messageSender =
                MessageSenderFactory.generateMessageSender(
                        attachmentUploader, null, ContextCompat.getMainExecutor(context));

        if (messageSender == null) return null;

        ChatIdChecker chatIdChecker = ChatIdCheckerGenerator.generateChatIdChecker();

        if (chatIdChecker == null) return null;

        ChatListBroadcastReceiver chatListBroadcastReceiver =
                ChatListBroadcastReceiver.getInstance(
                        null, null, chatIdChecker, messageSender);

        if (chatListBroadcastReceiver == null)
            return null;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ChatListAdapter chatListAdapter =
                ChatListAdapter.getInstance(
                        layoutInflater, null, null);

        return new ChatListFragment(
                callback, chatListLoader, chatListBroadcastReceiver, chatListAdapter);
    }

    public static ChatListFragment getInstance(
            final ChatListFragmentCallback callback,
            final ChatListLoaderBase chatListLoader,
            final ChatListBroadcastReceiver chatListBroadcastReceiver,
            final ChatListAdapter chatListAdapter)
    {
        if (callback == null || chatListLoader == null ||
            chatListBroadcastReceiver == null || chatListAdapter == null)
        {
            return null;
        }

        return new ChatListFragment(
                callback, chatListLoader, chatListBroadcastReceiver, chatListAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!launchChatsLoader()) return;

        m_chatListViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        m_chatChangeBroadcastReceiver.setNewMessageReceivedCallback(this);
        m_chatChangeBroadcastReceiver.setCommandSendingCallback(this);
        m_chatListLoader.setCallback(this);
        m_chatListAdapter.setChatListCallback(this);
        m_chatListAdapter.setChatListItemCallback(this);

        IntentFilter intentFilter = new IntentFilter(ChatListBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intentFilter.addAction(ChatListBroadcastReceiver.C_UPDATES_RECEIVED);
        intentFilter.addAction(ChatListBroadcastReceiver.C_SEND_COMMAND_MESSAGE);

        LocalBroadcastManager.
                getInstance(getActivity().getApplicationContext()).
                registerReceiver(
                        m_chatChangeBroadcastReceiver,
                    intentFilter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (m_chatListLoader != null)
            m_chatListLoader.cancel(true);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                .unregisterReceiver(m_chatChangeBroadcastReceiver);

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        m_dialogsListRecyclerView = view.findViewById(R.id.dialogs_recycler_view);

        m_dialogsListRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        m_dialogsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        m_dialogsListRecyclerView.setAdapter(m_chatListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDialogsLoaded() {
        m_callback.onDialogsLoaded();

        List<ChatEntity> dialogs = ChatsStore.getInstance().getChatList();

        if (!m_chatListAdapter.setDialogsList(dialogs)) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Dialogs list is empty!", true),
                    getContext().getApplicationContext()
            );

            return;
        }
    }

    @Override
    public void onDialogsLoadingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    @Override
    public void onRecyclerViewAdapterErrorOccurred(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    @Override
    public void onDialogItemClick(long peerId) {
        m_chatListViewModel.setCurrentChatId(peerId);

        Intent intent = new Intent(getActivity(), ChatActivity.class);

        intent.putExtra(ChatActivity.C_PEER_ID_EXTRA_PROP_NAME, peerId);

        startActivity(intent);
    }

    private boolean launchChatsLoader() {
        m_chatListLoader.execute();

        return true;
    }

    @Override
    public void onNewMessageReceived(long chatId) {
        onDialogsLoaded();

        if (m_chatListViewModel.getCurrentChatId() != chatId)
            return;

        Intent newMessagesIntent = new Intent(ChatBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        LocalBroadcastManager
                .getInstance(getActivity().getApplicationContext())
                .sendBroadcast(newMessagesIntent);
    }

    @Override
    public void onNewMessageReceivingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getActivity().getApplicationContext()
        );
    }

    @Override
    public void onNewCommandSendingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getActivity().getApplicationContext()
        );
    }
}
