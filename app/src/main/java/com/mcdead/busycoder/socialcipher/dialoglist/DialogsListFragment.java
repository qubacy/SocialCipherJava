package com.mcdead.busycoder.socialcipher.dialoglist;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.RecyclerViewAdapterErrorCallback;
import com.mcdead.busycoder.socialcipher.dialog.DialogActivity;
import com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader.DialogsLoaderBase;
import com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader.DialogsLoaderFactory;
import com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader.DialogsLoaderVK;
import com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader.DialogsLoadingCallback;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import java.util.List;

public class DialogsListFragment extends Fragment
    implements DialogsLoadingCallback,
        RecyclerViewAdapterErrorCallback,
        DialogItemClickedCallback
{
    private DialogsLoaderBase m_loaderTask = null;

    private RecyclerView m_dialogsListRecyclerView = null;
    private DialogListAdapter m_dialogsListAdapter = null;

    private DialogsBroadcastReceiver m_dialogChangeBroadcastReceiver = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!launchDialogsLoader()) return;

        m_dialogChangeBroadcastReceiver = new DialogsBroadcastReceiver(this);

        IntentFilter intentFilter =  new IntentFilter(DialogsBroadcastReceiver.C_NEW_MESSAGES_ADDED);

        intentFilter.addAction(DialogsBroadcastReceiver.C_UPDATES_RECEIVED);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
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
        View view = inflater.inflate(R.layout.fragment_dialogs_list, container, false);

        m_dialogsListRecyclerView = view.findViewById(R.id.dialogs_recycler_view);
        m_dialogsListAdapter = new DialogListAdapter(getActivity(), this, this);

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
        List<DialogEntity> dialogs = DialogsStore.getInstance().getDialogs();

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
        Intent intent = new Intent(getActivity(), DialogActivity.class);

        intent.putExtra(DialogActivity.C_PEER_ID_EXTRA_PROP_NAME, peerId);

        startActivity(intent);
    }

    private boolean launchDialogsLoader() {
        m_loaderTask = DialogsLoaderFactory.generateDialogsLoader(this);

        if (m_loaderTask == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("", true),
                    getActivity().getApplicationContext()
            );

            return false;
        }

        m_loaderTask.execute();

        return true;
    }
}
