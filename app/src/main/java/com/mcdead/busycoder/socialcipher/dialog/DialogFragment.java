package com.mcdead.busycoder.socialcipher.dialog;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.attachmentdoc.AttachmentDocUtility;
import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.attachmentshower.AttachmentShowerActivity;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.attachmentdoc.LinkedFileOpenerCallback;
import com.mcdead.busycoder.socialcipher.dialog.dialogloader.DialogLoaderBase;
import com.mcdead.busycoder.socialcipher.dialog.dialogloader.DialogLoaderFactory;
import com.mcdead.busycoder.socialcipher.dialog.dialogloader.DialogLoadingCallback;
import com.mcdead.busycoder.socialcipher.dialog.messagesender.MessageSenderBase;
import com.mcdead.busycoder.socialcipher.dialog.messagesender.MessageSenderFactory;
import com.mcdead.busycoder.socialcipher.dialog.messagesender.MessageSendingCallback;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DialogFragment extends Fragment
    implements
        DialogUpdatedCallback,
        DialogLoadingCallback,
        MessageListAdapterCallback,
        MessageListItemCallback,
        LinkedFileOpenerCallback,
        MessageSendingCallback,
        AttachmentPickerCallback
{
    private DialogFragmentCallback m_callback = null;

    private long m_peerId = 0;
    private long m_localPeerId = 0;

    private DialogBroadcastReceiver m_broadcastReceiver = null;

    private RecyclerView m_messagesList = null;
    private MessageListAdapter m_messagesAdapter = null;

    private EditText m_sendingMessageText = null;
    private List<AttachmentData> m_uploadingAttachmentList = null;

    public DialogFragment(
            final long peerId,
            DialogFragmentCallback callback)
    {
        super();

        m_peerId = peerId;
        m_localPeerId = UsersStore.getInstance().getLocalUser().getPeerId();
        m_callback = callback;

        m_uploadingAttachmentList = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_broadcastReceiver = new DialogBroadcastReceiver(this);
        m_messagesAdapter = new MessageListAdapter(
                getActivity(),
                this,
                m_localPeerId);

        LocalBroadcastManager
                .getInstance(getContext().getApplicationContext())
                .registerReceiver(m_broadcastReceiver,
                        new IntentFilter(DialogBroadcastReceiver.C_NEW_MESSAGE_ADDED));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        m_messagesList = view.findViewById(R.id.messages_list);

        m_messagesList.setAdapter(m_messagesAdapter);
        m_messagesList.setLayoutManager(new LinearLayoutManager(getContext()));
        //m_messagesList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        m_sendingMessageText = view.findViewById(R.id.dialog_message_sending_text);

        m_sendingMessageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                    TextView textView,
                    int i,
                    KeyEvent keyEvent)
            {
                if (keyEvent.getKeyCode() != KeyEvent.KEYCODE_ENTER)
                    return false;
                if (keyEvent.getAction() == KeyEvent.ACTION_UP)
                    return false;

                sendNewMessage();

                return true;
            }
        });

        view.findViewById(R.id.dialog_message_sending_attachments_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickAttachmentFiles();
                }
            });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Error error = initChat();

        if (error != null) {
            ErrorBroadcastReceiver.broadcastError(error,
                    getActivity().getApplicationContext());
        }
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
    public void onDialogUpdatingError(final Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    @Override
    public void onErrorOccurred(final Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    @Override
    public void onAttachmentsShowClicked(final MessageEntity message) {
        if (message == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message Data hasn't been provided!", true),
                            getActivity().getApplicationContext()
                    );

            return;
        }

        List<AttachmentEntityBase> messageAttachments = message.getAttachments();

        if (messageAttachments == null) return;

        showAttachmentsShower(messageAttachments);
    }

    private void showAttachmentsShower(
            final List<AttachmentEntityBase> messageAttachments)
    {
        Intent intent = new Intent(
                getActivity().getApplicationContext(),
                AttachmentShowerActivity.class);
        Bundle args = new Bundle();

        args.putSerializable(
                AttachmentShowerActivity.C_ATTACHMENT_LIST_PROP_NAME,
                (Serializable) messageAttachments);
        intent.putExtra(AttachmentShowerActivity.C_ATTACHMENT_LIST_WRAPPER_PROP_NAME, args);

        startActivity(intent);
    }

    @Override
    public void onDialogLoaded() {
        m_callback.onDialogLoaded();

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

        } else {
            onDialogLoaded();
        }

        return null;
    }

    private void pickAttachmentFiles() {
        DialogFragmentCallback dialogActivity = (DialogFragmentCallback) getActivity();

        if (dialogActivity == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("DialogFragmentCallback hasn't been derived!", true),
                            getActivity().getApplicationContext()
                    );

            return;
        }

        dialogActivity.onAttachmentPickerDemanded();
    }

    private void sendNewMessage() {
        String text = m_sendingMessageText.getText().toString();

        MessageSenderBase messageSender
                = MessageSenderFactory.generateMessageSender(
                        m_peerId,
                        text,
                        m_uploadingAttachmentList,
                        this,
                        getContext().getContentResolver());

        if (messageSender == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message Sender hasn't been initialized!", true),
                            getActivity().getApplicationContext());

            return;
        }

        m_sendingMessageText.getText().clear();
        messageSender.execute();
    }

//    @Override
//    public void onLinkedAttachmentClicked(Uri uri) {
//        if (uri.getPath().isEmpty()) return;
//
//        (new LinkedFileOpener(uri, getActivity(), this)).execute();
//    }

    @Override
    public void onFileOpeningFail(final Uri fileUri) {
        AttachmentDocUtility.showFileShowingFailedToast(getActivity(), fileUri);
    }

    @Override
    public void onFileOpeningError(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(error, getActivity().getApplicationContext());
    }

    @Override
    public void onMessageSent() {
        // todo: what to do?

//        Toast.makeText(getActivity(),
//                "Message has been sent!",
//                Toast.LENGTH_SHORT)
//                .show();
    }

    @Override
    public void onMessageSendingError(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(error, getActivity().getApplicationContext());
    }

    @Override
    public void onAttachmentFilesPicked(final List<AttachmentData> pickedFileUriList) {
        m_uploadingAttachmentList = pickedFileUriList;
    }
}
