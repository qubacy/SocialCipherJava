package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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

import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter.ChatListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter.ChatListAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter.ChatListItemCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.model.ChatListViewModel;
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
    private ChatListViewModel m_chatListViewModel = null;

    private ChatListFragmentCallback m_callback = null;
    private ChatListLoaderBase m_chatListLoader = null;
    private ChatListBroadcastReceiver m_chatChangeBroadcastReceiver = null;
    private ChatListAdapter m_chatListAdapter = null;

    private RecyclerView m_dialogsListRecyclerView = null;

    private Context m_context = null;

    public ChatListFragment() {
        super();
    }

    protected ChatListFragment(
            final ChatListFragmentCallback callback,
            final ChatListLoaderBase chatListLoader)
    {
        super();

        m_callback = callback;
        m_chatListLoader = chatListLoader;
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

        return new ChatListFragment(callback, chatListLoader);
    }

    public static ChatListFragment getInstance(
            final ChatListFragmentCallback callback,
            final ChatListLoaderBase chatListLoader)
    {
        if (callback == null || chatListLoader == null) {
            return null;
        }

        return new ChatListFragment(
                callback, chatListLoader);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_chatListViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        if (!m_chatListViewModel.isInitialized()) {
            m_chatListLoader.setCallback(this);

            m_chatListViewModel.setChatListLoader(m_chatListLoader);
            m_chatListViewModel.setCallback(m_callback);

        } else {
            m_chatListLoader = m_chatListViewModel.getChatLoader();
            m_callback = m_chatListViewModel.getCallback();
        }

        launchChatsLoader();

        Error broadcastReceiverSettingError = setupChatListBroadcastReceiver();

        if (broadcastReceiverSettingError != null) {
            ErrorBroadcastReceiver.broadcastError(
                    broadcastReceiverSettingError, m_context.getApplicationContext());

            return;
        }
    }

    private Error setupChatListBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ChatListBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intentFilter.addAction(ChatListBroadcastReceiver.C_UPDATES_RECEIVED);
        intentFilter.addAction(ChatListBroadcastReceiver.C_SEND_COMMAND_MESSAGE);

        ChatListBroadcastReceiver chatListBroadcastReceiver =
                ChatListBroadcastReceiver.getInstance(
                        this, this, ContextCompat.getMainExecutor(m_context));

        if (chatListBroadcastReceiver == null)
            return new Error(
                    "Chat List Broadcast Receiver hasn't been initialized!", true);

        m_chatChangeBroadcastReceiver = chatListBroadcastReceiver;

        LocalBroadcastManager.
                getInstance(m_context.getApplicationContext()).
                registerReceiver(m_chatChangeBroadcastReceiver, intentFilter);

        return null;
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

        LocalBroadcastManager.
                getInstance(m_context.getApplicationContext()).
                unregisterReceiver(m_chatChangeBroadcastReceiver);

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
                new DividerItemDecoration(m_context, DividerItemDecoration.VERTICAL));
        m_dialogsListRecyclerView.setLayoutManager(new LinearLayoutManager(m_context));

        ChatListAdapter chatListAdapter =
                ChatListAdapter.getInstance(inflater, this, this);

        if (chatListAdapter == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Chat List Adapter generation has been failed!",
                            true), m_context.getApplicationContext());

            return view;
        }

        m_chatListAdapter = chatListAdapter;

        m_dialogsListRecyclerView.setAdapter(m_chatListAdapter);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        m_context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        m_dialogsListRecyclerView.setAdapter(null);

        super.onDestroyView();
    }

    @Override
    public void onDialogsLoaded() {
        m_callback.onChatListLoaded();

        m_chatListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogsLoadingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }

    @Override
    public ChatEntity getChatByIndex(int index) {
        ChatEntity chat = ChatsStore.getInstance().getChatByIndex(index);

        if (chat == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Chat hasn't been found!",
                            true),
                    m_context.getApplicationContext());

            return null;
        }

        return chat;
    }

    @Override
    public int getChatListSize() {
        return ChatsStore.getInstance().getChatList().size();
    }

    @Override
    public void onRecyclerViewAdapterErrorOccurred(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }

    @Override
    public void onDialogItemClick(final long chatId) {
        m_callback.onChatItemClicked(chatId);

        m_chatListViewModel.setCurrentChatId(chatId);
    }

    private boolean launchChatsLoader() {
        if (m_chatListViewModel.isChatListLoadingStarted())
            return true;

        m_chatListViewModel.setChatListLoadingStarted();
        m_chatListLoader.execute();

        return true;
    }

    @Override
    public void onNewMessageReceived(final long chatId) {
        onDialogsLoaded();

        Long curChatId = m_chatListViewModel.getCurrentChatId();

        if (curChatId == null) return;
        if (curChatId != chatId) return;

        Intent newMessagesIntent = new Intent(ChatBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        LocalBroadcastManager
                .getInstance(m_context.getApplicationContext())
                .sendBroadcast(newMessagesIntent);
    }

    @Override
    public void onNewMessageReceivingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }

    @Override
    public void onNewCommandSendingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }
}
