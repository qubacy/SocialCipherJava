package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoaderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoaderFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader.ChatListLoadingCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;

import java.util.List;

public class ChatListFragment extends Fragment
    implements ChatListLoadingCallback,
        ChatListAdapterCallback,
        ChatListItemCallback,
        NewMessageReceivedCallback,
        CommandSendingCallback
{
    private ChatListFragmentCallback m_callback = null;

    private ChatListViewModel m_dialogsViewModel = null;

    private ChatListLoaderBase m_loaderTask = null;

    private RecyclerView m_dialogsListRecyclerView = null;
    private ChatListAdapter m_dialogsListAdapter = null;

    private ChatListBroadcastReceiver m_dialogChangeBroadcastReceiver = null;

    public ChatListFragment(
            ChatListFragmentCallback callback)
    {
        m_callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!launchDialogsLoader()) return;

        m_dialogsViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        m_dialogChangeBroadcastReceiver = new ChatListBroadcastReceiver(this, this);

        IntentFilter intentFilter =  new IntentFilter(ChatListBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intentFilter.addAction(ChatListBroadcastReceiver.C_UPDATES_RECEIVED);
        intentFilter.addAction(ChatListBroadcastReceiver.C_SEND_COMMAND_MESSAGE);

        LocalBroadcastManager.
                getInstance(getActivity().getApplicationContext()).
                registerReceiver(
                    m_dialogChangeBroadcastReceiver,
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
        if (m_loaderTask != null)
            m_loaderTask.cancel(true);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                .unregisterReceiver(m_dialogChangeBroadcastReceiver);

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
        m_dialogsListAdapter = new ChatListAdapter(getActivity(), this, this);

        m_dialogsListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        m_dialogsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        m_dialogsListRecyclerView.setAdapter(m_dialogsListAdapter);

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

        if (!m_dialogsListAdapter.setDialogsList(dialogs)) {
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
        m_dialogsViewModel.setCurrentChatId(peerId);

        Intent intent = new Intent(getActivity(), ChatActivity.class);

        intent.putExtra(ChatActivity.C_PEER_ID_EXTRA_PROP_NAME, peerId);

        startActivity(intent);
    }

    private boolean launchDialogsLoader() {
        m_loaderTask = ChatListLoaderFactory.generateChatListLoader(this);

        if (m_loaderTask == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Dialogs loader hasn't been launched!", true),
                    getActivity().getApplicationContext()
            );

            return false;
        }

        m_loaderTask.execute();

        return true;
    }

    @Override
    public void onNewMessageReceived(long chatId) {
        onDialogsLoaded();

        if (m_dialogsViewModel.getCurrentChatId() != chatId)
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
