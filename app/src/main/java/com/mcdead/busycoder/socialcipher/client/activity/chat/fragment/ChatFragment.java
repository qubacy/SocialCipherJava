package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment;

import static com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog.RequestAnswerDialogFragment.C_FRAGMENT_TAG;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.AttachmentPickerCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiverCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.MessageListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.MessageListAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.MessageListItemCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog.RequestAnswerDialogFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog.RequestAnswerDialogFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc.AttachmentDocUtility;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.AttachmentShowerActivity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.side.ChatSide;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.side.ChatSideDefiner;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.side.ChatSideDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc.LinkedFileOpenerCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoadingCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSendingCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.cipher.MessageCipherProcessor;
import com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorServiceBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswerType;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment
    implements
        ChatBroadcastReceiverCallback,
        ChatLoadingCallback,
        MessageListAdapterCallback,
        MessageListItemCallback,
        LinkedFileOpenerCallback,
        MessageSendingCallback,
        AttachmentPickerCallback,
        RequestAnswerDialogFragmentCallback
{
    private ChatFragmentCallback m_callback = null;

    private long m_peerId = 0;
    private long m_localPeerId = 0;

    private ChatBroadcastReceiver m_broadcastReceiver = null;

    private RecyclerView m_messagesList = null;
    private MessageListAdapter m_messagesAdapter = null;

    private EditText m_sendingMessageText = null;
    private List<AttachmentData> m_uploadingAttachmentList = null;

    private AppCompatImageButton m_cipherButton = null;

    public ChatFragment(
            final long peerId,
            ChatFragmentCallback callback)
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

        m_broadcastReceiver = new ChatBroadcastReceiver(this);
        m_messagesAdapter = new MessageListAdapter(
                getActivity(),
                this,
                m_localPeerId);

        IntentFilter intentFilter = new IntentFilter(ChatBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intentFilter.addAction(ChatBroadcastReceiver.C_CIPHER_SESSION_SETTING_ENDED);
        intentFilter.addAction(ChatBroadcastReceiver.C_SEND_NEW_MESSAGE);
        intentFilter.addAction(ChatBroadcastReceiver.C_SETTING_CIPHER_SESSION_ANSWER_REQUESTED);
        intentFilter.addAction(ChatBroadcastReceiver.C_SHOW_NEW_CHAT_NOTIFICATION);

        LocalBroadcastManager
                .getInstance(getContext().getApplicationContext())
                .registerReceiver(m_broadcastReceiver, intentFilter);
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

        m_cipherButton = view.findViewById(R.id.dialog_message_sending_ciphering_button);

        m_cipherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCipherButtonClicked();
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
    public void onNewChatMessageReceived() {
        ChatEntity dialog = ChatsStore.getInstance().getChatByPeerId(m_peerId);
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
    public void onChatBroadcastReceiverErrorOccurred(final Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    @Override
    public void onSettingCipherSessionAnswerRequested(
            final long chatId,
            final long initializerPeerId,
            final long messageId)
    {
        if (chatId != m_peerId) {
            onRequestAnswerDialogResultGotten(new CipherRequestAnswerSettingSession(messageId,false));

            return;
        }

        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null) {
            onErrorOccurred(new Error("Users Store hasn't been initialized!", true));

            return;
        }

        UserEntity initializerData = usersStore.getUserByPeerId(initializerPeerId);

        if (initializerData == null) {
            onErrorOccurred(new Error("Initializer User Data was null!", true));

            return;
        }

        String requestText =
                String.format(
                        RequestAnswerType.SETTING_CIPHER_SESSION.getText(),
                        initializerData.getName());
        RequestAnswerDialogFragment dialogFragment =
                RequestAnswerDialogFragment.getInstance(
                        RequestAnswerType.SETTING_CIPHER_SESSION,
                        messageId,
                        requestText,
                        this);
        FragmentManager fragmentManager = getParentFragmentManager();

        dialogFragment.show(fragmentManager, C_FRAGMENT_TAG);
    }

    @Override
    public void onCipherSessionSettingEnded(
            final long chatId,
            final boolean isCipherSessionSet)
    {
        if (chatId != m_peerId) return;

        // todo: enabling some identification showing current chat ciphering state..

        onNewChatNotificationShowingRequested((isCipherSessionSet ? "Session set!" : "Session hasn't been set!"));

        onCipherButtonEnabledChange(true);
    }

    @Override
    public void onNewMessageSendingRequested(
            long chatId,
            String messageText)
    {
        if (chatId != m_peerId) return;

        MessageSenderBase messageSender =
                MessageSenderFactory.generateMessageSender(
                    chatId,
                    messageText,
                    null,
                    null,
                    null);

        if (messageSender == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message Sender hasn't been initialized!", true),
                            getActivity().getApplicationContext());

            return;
        }

        messageSender.execute();
    }

    @Override
    public void onNewChatNotificationShowingRequested(
            final String chatNotificationText)
    {
        // todo: showing chat notification..

        Toast.makeText(getContext(), chatNotificationText, Toast.LENGTH_LONG).show();
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

        ChatEntity dialog = ChatsStore.getInstance().getChatByPeerId(m_peerId);

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
        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);

        ChatEntity dialog = dialogsStore.getChatByPeerId(m_peerId);

        if (dialog == null)
            return new Error("Dialog hasn't been found!", true);

        if (!dialog.areAttachmentsLoaded()) {
            ChatLoaderBase dialogLoader
                    = ChatLoaderFactory.generateDialogLoader(this, m_peerId);

            if (dialogLoader == null)
                return new Error("Dialog loader hasn't been initialized!", true);

            dialogLoader.execute();

        } else {
            onDialogLoaded();
        }

        return null;
    }

    private void onCipherButtonClicked() {
        // todo: checking chatId..

        ChatSideDefiner chatSideDefiner = ChatSideDefinerFactory.generateChatSideDefiner();

        if (chatSideDefiner == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Chat Side Definer creating process has been failed!", true),
                    getActivity().getApplicationContext());

            return;
        }

        ChatSide chatSide = chatSideDefiner.defineChatSide(m_peerId);

        if (chatSide == ChatSide.LOCAL)
            return;

        onCipherButtonEnabledChange(false);

        // todo: initializing a new ciphering session..

        Intent intent =
                new Intent(
                        CommandProcessorServiceBroadcastReceiver.C_INITIALIZE_NEW_CIPHERING_SESSION);

        intent.putExtra(CommandProcessorServiceBroadcastReceiver.C_CHAT_ID_PROP_NAME, m_peerId);

        LocalBroadcastManager.
                getInstance(getContext().getApplicationContext()).
                sendBroadcast(intent);
    }

    private void pickAttachmentFiles() {
        ChatFragmentCallback dialogActivity = (ChatFragmentCallback) getActivity();

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
        List<AttachmentData> attachmentDataList = new ArrayList<>(m_uploadingAttachmentList);

        if (text.isEmpty() && m_uploadingAttachmentList.isEmpty()) {
            // todo: may be it needs to show something..

            return;
        }

        MessageCipherProcessor messageCipherProcessor =
                MessageCipherProcessor.getInstance(m_peerId);

        if (messageCipherProcessor != null) {
            Error cipheringError;

            if (!text.isEmpty()) {
                ObjectWrapper<Pair<Boolean, String>> processedSuccessFlagText = new ObjectWrapper<>();
                cipheringError = messageCipherProcessor.processText(
                        text, true, processedSuccessFlagText);

                if (cipheringError != null) {
                    ErrorBroadcastReceiver.broadcastError(cipheringError,
                            getActivity().getApplicationContext());

                    return;
                }

                text = processedSuccessFlagText.getValue().second;
            }

            if (!attachmentDataList.isEmpty()) {
                ObjectWrapper<List<AttachmentData>> processedAttachmentData = new ObjectWrapper<>();
                cipheringError =
                        messageCipherProcessor.processAttachmentData(
                                attachmentDataList, true, processedAttachmentData);

                if (cipheringError != null) {
                    ErrorBroadcastReceiver.broadcastError(cipheringError,
                            getActivity().getApplicationContext());

                    return;
                }

                attachmentDataList = processedAttachmentData.getValue();
            }
        }

        AttachmentUploaderSyncBase attachmentUploader =
                AttachmentUploaderSyncFactory.generateAttachmentUploader(
                        m_peerId,
                        getContext().getContentResolver());

        if (attachmentUploader == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Attachment Uploader hasn't been initialized!", true),
                            getActivity().getApplicationContext());

            return;
        }

        MessageSenderBase messageSender =
                MessageSenderFactory.generateMessageSender(
                        m_peerId,
                        text,
                        attachmentDataList,
                        attachmentUploader,
                        this);

        if (messageSender == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message Sender hasn't been initialized!", true),
                            getActivity().getApplicationContext());

            return;
        }

        m_sendingMessageText.getText().clear();
        m_uploadingAttachmentList.clear();

        messageSender.execute();
    }

    @Override
    public void onFileOpeningFail(final Uri fileUri) {
        AttachmentDocUtility.showFileShowingFailedToast(getActivity(), fileUri);
    }

    @Override
    public void onFileOpeningError(
            final Error error)
    {
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
    public void onMessageSendingError(
            final Error error)
    {
        ErrorBroadcastReceiver
                .broadcastError(error, getActivity().getApplicationContext());
    }

    @Override
    public void onAttachmentFilesPicked(
            final List<AttachmentData> pickedFileUriList)
    {
        m_uploadingAttachmentList = pickedFileUriList;
    }

    @Override
    public void onRequestAnswerDialogResultGotten(
            final RequestAnswer requestAnswer)
    {
        Intent intent = new Intent(CommandProcessorServiceBroadcastReceiver.C_PROVIDE_REQUEST_ANSWER);

        intent.putExtra(CommandProcessorServiceBroadcastReceiver.C_REQUEST_ANSWER_PROP_NAME, requestAnswer);

        LocalBroadcastManager.
                getInstance(getActivity().getApplicationContext()).
                sendBroadcast(intent);
    }

    @Override
    public void onRequestAnswerDialogErrorOccurred(
            final Error error)
    {
        onErrorOccurred(error);
    }

    private void onCipherButtonEnabledChange(final boolean isEnabled) {
        m_cipherButton.setEnabled(isEnabled);

        if (isEnabled)
            m_cipherButton.setBackgroundResource(R.drawable.message_sending_button_shape);
        else
            m_cipherButton.setBackgroundResource(R.drawable.message_sending_button_shape_pressed);
    }
}
