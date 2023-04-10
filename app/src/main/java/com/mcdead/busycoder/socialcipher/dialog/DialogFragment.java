package com.mcdead.busycoder.socialcipher.dialog;

import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.RecyclerViewAdapterErrorCallback;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.dialog.dialogfile.LinkedFileOpener;
import com.mcdead.busycoder.socialcipher.dialog.dialogfile.LinkedFileOpenerCallback;
import com.mcdead.busycoder.socialcipher.dialog.dialogloader.DialogLoaderBase;
import com.mcdead.busycoder.socialcipher.dialog.dialogloader.DialogLoaderFactory;
import com.mcdead.busycoder.socialcipher.dialog.dialogloader.DialogLoadingCallback;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;

import java.util.List;

public class DialogFragment extends Fragment
    implements
        DialogUpdatedCallback,
        DialogLoadingCallback,
        RecyclerViewAdapterErrorCallback,
        AttachmentExternalLinkClickedCallback,
        LinkedFileOpenerCallback
{
    private long m_peerId = 0;
    private long m_localPeerId = 0;

    private DialogBroadcastReceiver m_broadcastReceiver = null;

    private RecyclerView m_messagesList = null;
    private MessageListAdapter m_messagesAdapter = null;

    public DialogFragment(final long peerId) {
        super();

        m_peerId = peerId;
        m_localPeerId = UsersStore.getInstance().getLocalUser().getPeerId();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_broadcastReceiver = new DialogBroadcastReceiver(this);

        LocalBroadcastManager
                .getInstance(getContext().getApplicationContext())
                .registerReceiver(m_broadcastReceiver,
                        new IntentFilter(DialogBroadcastReceiver.C_NEW_MESSAGE_ADDED));

        Error error = initChat();

        if (error != null) {
            ErrorBroadcastReceiver.broadcastError(error,
                    getActivity().getApplicationContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        m_messagesList = view.findViewById(R.id.messages_list);
        m_messagesAdapter = new MessageListAdapter(
                getActivity(),
                this,
                this,
                m_localPeerId);

        m_messagesList.setAdapter(m_messagesAdapter);
        m_messagesList.setLayoutManager(new LinearLayoutManager(getContext()));
        //m_messagesList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        onDialogLoaded();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager
                .getInstance(getContext().getApplicationContext())
                .unregisterReceiver(m_broadcastReceiver);

        super.onDestroy();
    }

    @Override
    public void onNewDialogMessageReceived() {
        DialogEntity dialog = DialogsStore.getInstance().getDialogByPeerId(m_peerId);
        List<MessageEntity> messageList = dialog.getMessages();

        if (!m_messagesAdapter.addNewMessage(messageList.get(messageList.size() - 1))) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Dialog doesn't exist!", true),
                    getContext().getApplicationContext()
            );

            return;
        }

        m_messagesList.scrollToPosition(m_messagesAdapter.getItemCount() - 1);
    }

    @Override
    public void onDialogUpdatingError(Error error) {
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
    public void onDialogLoaded() {
        DialogEntity dialog = DialogsStore.getInstance().getDialogByPeerId(m_peerId);

        if (!m_messagesAdapter.setMessagesList(dialog.getMessages())) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Dialog doesn't exist!", true),
                    getContext().getApplicationContext()
            );

            return;
        }

        m_messagesList.scrollToPosition(dialog.getMessages().size() - 1);
    }

    @Override
    public void onDialogLoadingError(Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    private Error initChat() {
        DialogsStore dialogsStore = DialogsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);

        DialogEntity dialog = dialogsStore.getDialogByPeerId(m_peerId);

        if (dialog == null)
            return new Error("Dialog hasn't been found!", true);

        if (!dialog.areAttachmentsLoaded()) {
            DialogLoaderBase dialogLoader
                    = DialogLoaderFactory.generateDialogLoader(this, m_peerId);

            if (dialogLoader == null)
                return new Error("Dialog loader hasn't been initialized!", true);

            dialogLoader.execute();
        }

        return null;
    }

    @Override
    public void onLinkClicked(Uri uri) {
        if (uri.getPath().isEmpty()) return;

        (new LinkedFileOpener(uri, getActivity(), this)).execute();
    }

    @Override
    public void onFileOpeningFail(final Uri fileUri) {
        Toast.makeText(
                getActivity(),
                "File is available here: " + fileUri.getPath(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFileOpeningError(Error error) {
        ErrorBroadcastReceiver
                .broadcastError(error, getActivity());
    }
}
