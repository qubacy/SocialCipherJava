package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment;

import static com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog.RequestAnswerDialogFragment.C_FRAGMENT_TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.AttachmentPickerCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.ChatActivity;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiverCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.attachmentlist.AttachmentListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.attachmentlist.AttachmentListAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist.MessageListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist.MessageListAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist.MessageListItemCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.model.ChatViewModel;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog.RequestAnswerDialogFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog.RequestAnswerDialogFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc.AttachmentDocUtility;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.AttachmentShowerActivity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.side.ChatSide;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.side.ChatSideDefiner;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.side.ChatSideDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.id.UserIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.id.UserIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.doc.opener.LinkedFileOpenerCallback;
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
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.sender.data.MessageToSendData;
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
        RequestAnswerDialogFragmentCallback,
        AttachmentListAdapterCallback
{
    private ChatViewModel m_chatViewModel = null;

    private long m_chatId;
    private long m_localPeerId;
    private ChatFragmentCallback m_callback;

    private ChatLoaderBase m_chatLoader;
    private AttachmentUploaderSyncBase m_attachmentUploader;
    private MessageSenderBase m_messageSender;

    private ChatBroadcastReceiver m_broadcastReceiver = null;

    private MessageListAdapter m_messageListAdapter = null;
    private AttachmentListAdapter m_attachmentListAdapter = null;

    private Context m_context = null;
    private RecyclerView m_messagesListView = null;
    private RecyclerView m_attachmentListView = null;
    private EditText m_sendingMessageText = null;
    private AppCompatImageButton m_cipherButton = null;

    public ChatFragment() {
        super();
    }

    protected ChatFragment(
            final long chatId,
            final long localPeerId,
            final ChatFragmentCallback callback,
            final ChatLoaderBase chatLoader,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSenderBase messageSender)
    {
        super();

        m_chatId = chatId;
        m_localPeerId = localPeerId;
        m_callback = callback;

        m_chatLoader = chatLoader;
        m_attachmentUploader = attachmentUploader;
        m_messageSender = messageSender;
    }

    public static ChatFragment getInstance(
            final long chatId,
            final ChatFragmentCallback callback,
            final Context context)
    {
        if (callback == null || context == null)
            return null;

        ChatIdChecker chatIdChecker = ChatIdCheckerGenerator.generateChatIdChecker();
        UserEntity localUser = UsersStore.getInstance().getLocalUser();

        if (chatIdChecker == null || localUser == null)
            return null;
        if (!chatIdChecker.isValid(chatId))
            return null;

        ChatLoaderBase chatLoader =
                ChatLoaderFactory.generateChatLoader(chatId, null);
        AttachmentUploaderSyncBase attachmentUploader =
                AttachmentUploaderSyncFactory.generateAttachmentUploader(null);
        MessageSenderBase messageSender =
                MessageSenderFactory.generateMessageSender(
                        attachmentUploader, null, ContextCompat.getMainExecutor(context));

        if (chatLoader == null || attachmentUploader == null || messageSender == null)
            return null;

        return new ChatFragment(
                chatId, localUser.getPeerId(), callback,
                chatLoader, attachmentUploader, messageSender);
    }

    public static ChatFragment getInstance(
            final long chatId,
            final long localPeerId,
            final ChatFragmentCallback callback,
            final ChatLoaderBase chatLoader,
            final AttachmentUploaderSyncBase attachmentUploader,
            final MessageSenderBase messageSender)
    {
        if (chatLoader == null || attachmentUploader == null ||
            messageSender == null || callback == null)
        {
            return null;
        }

        ChatIdChecker chatIdChecker = ChatIdCheckerGenerator.generateChatIdChecker();
        UserIdChecker userIdChecker = UserIdCheckerGenerator.generateUserIdChecker();

        if (userIdChecker == null || chatIdChecker == null) return null;
        if (!userIdChecker.isValid(chatId) || !chatIdChecker.isValid(localPeerId)) return null;

        return new ChatFragment(
                chatId, localPeerId, callback, chatLoader,
                attachmentUploader, messageSender);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        if (!m_chatViewModel.isInitialized()) {
            m_messageSender.setCallback(this);
            m_chatLoader.setCallback(this);
            m_attachmentUploader.setContentResolver(m_context.getContentResolver());

            m_chatViewModel.setChatId(m_chatId);
            m_chatViewModel.setLocalPeerId(m_localPeerId);
            m_chatViewModel.setCallback(m_callback);
            m_chatViewModel.setMessageSender(m_messageSender);
            m_chatViewModel.setChatLoader(m_chatLoader);
            m_chatViewModel.setAttachmentUploader(m_attachmentUploader);
            m_chatViewModel.setWaitingForCipherSessionSet(false);

        } else {
            m_chatId = m_chatViewModel.getChatId();
            m_localPeerId = m_chatViewModel.getLocalPeerId();
            m_messageSender = m_chatViewModel.getMessageSender();
            m_chatLoader = m_chatViewModel.getChatLoader();
            m_attachmentUploader = m_chatViewModel.getAttachmentUploader();
            m_callback = m_chatViewModel.getCallback();

        }

        Error broadcastSettingError = setupChatBroadcastReceiver();

        if (broadcastSettingError != null) {
            ErrorBroadcastReceiver.broadcastError(
                    broadcastSettingError, m_context.getApplicationContext());

            return;
        }
    }

    private Error setupChatBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ChatBroadcastReceiver.C_NEW_MESSAGE_ADDED);

        intentFilter.addAction(ChatBroadcastReceiver.C_CIPHER_SESSION_SETTING_ENDED);
        intentFilter.addAction(ChatBroadcastReceiver.C_SEND_NEW_MESSAGE);
        intentFilter.addAction(ChatBroadcastReceiver.C_SETTING_CIPHER_SESSION_ANSWER_REQUESTED);
        intentFilter.addAction(ChatBroadcastReceiver.C_SHOW_NEW_CHAT_NOTIFICATION);

        ChatBroadcastReceiver chatBroadcastReceiver = ChatBroadcastReceiver.getInstance(this);

        if (chatBroadcastReceiver == null)
            return new Error(
                    "Chat Broadcast Receiver generation has been failed!", true);

        m_broadcastReceiver = chatBroadcastReceiver;

        LocalBroadcastManager
                .getInstance(m_context.getApplicationContext())
                .registerReceiver(m_broadcastReceiver, intentFilter);

        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        m_messagesListView = view.findViewById(R.id.messages_list);
        m_attachmentListView = view.findViewById(R.id.attachment_list);

        m_messageListAdapter = MessageListAdapter.getInstance(inflater, this, m_localPeerId);
        m_attachmentListAdapter = AttachmentListAdapter.getInstance(inflater, this);

        m_messagesListView.setLayoutManager(new LinearLayoutManager(m_context));
        m_attachmentListView.setLayoutManager(
                new LinearLayoutManager(
                        m_context,
                        LinearLayoutManager.HORIZONTAL,
                        false));

        m_messagesListView.setAdapter(m_messageListAdapter);
        m_attachmentListView.setAdapter(m_attachmentListAdapter);

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

        onCipherButtonEnabledChange(!m_chatViewModel.isWaitingForCipherSessionSet());

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
                    m_context.getApplicationContext());
        }

        processAttachmentListChange();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        m_context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        m_context = null;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager
                .getInstance(m_context.getApplicationContext())
                .unregisterReceiver(m_broadcastReceiver);

        super.onDestroy();
    }

    @Override
    public void onNewChatMessageReceived() {
        ChatEntity chat = ChatsStore.getInstance().getChatById(m_chatId);
        List<MessageEntity> messageList = chat.getMessages();
        int insertedMessageIndex = messageList.size() - 1;

        m_messageListAdapter.notifyItemInserted(insertedMessageIndex);
        m_messagesListView.scrollToPosition(insertedMessageIndex);
    }

    @Override
    public void onChatBroadcastReceiverErrorOccurred(final Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }

    @Override
    public void onSettingCipherSessionAnswerRequested(
            final long chatId,
            final long initializerPeerId,
            final long messageId)
    {
        if (chatId != m_chatId) {
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
        if (chatId != m_chatId) return;

        // todo: enabling some identification showing current chat ciphering state..

        onNewChatNotificationShowingRequested(
                (isCipherSessionSet ? "Session set!" : "Session hasn't been set!"));

        m_chatViewModel.setWaitingForCipherSessionSet(false);
        onCipherButtonEnabledChange(true);
    }

    @Override
    public void onNewMessageSendingRequested(
            long chatId,
            String messageText)
    {
        if (chatId != m_chatId) return;

        MessageToSendData messageToSendData =
                MessageToSendData.getInstance(chatId, messageText, null);

        if (messageToSendData == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message Can't be sent!", false),
                            m_context.getApplicationContext());

            return;
        }

        m_messageSender.execute(messageToSendData);
    }

    @Override
    public void onNewChatNotificationShowingRequested(
            final String chatNotificationText)
    {
        // todo: showing chat notification..

        Toast.makeText(m_context, chatNotificationText, Toast.LENGTH_LONG).show();
    }

    @Override
    public MessageEntity getMessageByIndex(int index) {
        ChatEntity chat = ChatsStore.getInstance().getChatById(m_chatId);

        if (chat == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Chat hasn't been found!", true),
                    m_context.getApplicationContext());

            return null;
        }

        return chat.getMessageByIndex(index);
    }

    @Override
    public int getMessageListSize() {
        ChatEntity chat = ChatsStore.getInstance().getChatById(m_chatId);

        if (chat == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Chat hasn't been found!", true),
                    m_context.getApplicationContext());

            return 0;
        }

        return chat.getMessages().size();
    }

    @Override
    public void onErrorOccurred(final Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }

    @Override
    public void onAttachmentsShowClicked(final MessageEntity message) {
        if (message == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message Data hasn't been provided!", true),
                            m_context.getApplicationContext()
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
                m_context.getApplicationContext(),
                AttachmentShowerActivity.class);
        Bundle args = new Bundle();

        args.putSerializable(
                AttachmentShowerActivity.C_ATTACHMENT_LIST_PROP_NAME,
                (Serializable) messageAttachments);
        intent.putExtra(AttachmentShowerActivity.C_ATTACHMENT_LIST_WRAPPER_PROP_NAME, args);

        startActivity(intent);
    }

    @Override
    public void onChatLoaded() {
        m_callback.onChatLoaded();

        ChatEntity chat = ChatsStore.getInstance().getChatById(m_chatId);

        if (chat == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Chat hasn't been found!", true),
                    m_context.getApplicationContext());

            return;
        }

        m_messageListAdapter.notifyDataSetChanged();
        m_messagesListView.scrollToPosition(chat.getMessages().size() - 1);
    }

    @Override
    public void onChatLoadingError(final Error error) {
        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
        );
    }

    private Error initChat() {
        ChatsStore chatsStore = ChatsStore.getInstance();

        if (chatsStore == null)
            return new Error("Chats Store hasn't been initialized!", true);

        ChatEntity chat = chatsStore.getChatById(m_chatId);

        if (chat == null)
            return new Error("Chat hasn't been found!", true);

        if (!chat.areAttachmentsLoaded()) {
            m_chatLoader.execute();

        } else {
            onChatLoaded();
        }

        return null;
    }

    private void onCipherButtonClicked() {
        // todo: checking chatId..

        ChatSideDefiner chatSideDefiner = ChatSideDefinerFactory.generateChatSideDefiner();

        if (chatSideDefiner == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Chat Side Definer creating process has been failed!",
                            true),
                    m_context.getApplicationContext());

            return;
        }

        ChatSide chatSide = chatSideDefiner.defineChatSide(m_chatId);

        if (chatSide == ChatSide.LOCAL)
            return;

        m_chatViewModel.setWaitingForCipherSessionSet(true);

        onCipherButtonEnabledChange(false);

        // todo: initializing a new ciphering session..

        Intent intent =
                new Intent(
                        CommandProcessorServiceBroadcastReceiver.C_INITIALIZE_NEW_CIPHERING_SESSION);

        intent.putExtra(CommandProcessorServiceBroadcastReceiver.C_CHAT_ID_PROP_NAME, m_chatId);

        LocalBroadcastManager.
                getInstance(m_context.getApplicationContext()).
                sendBroadcast(intent);
    }

    private void pickAttachmentFiles() {
        ChatFragmentCallback dialogActivity = (ChatFragmentCallback) getActivity();

        if (dialogActivity == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error(
                                    "DialogFragmentCallback hasn't been derived!",
                                    true),
                            m_context.getApplicationContext()
                    );

            return;
        }

        dialogActivity.onAttachmentPickerDemanded();
    }

    private void sendNewMessage() {
        String text = m_sendingMessageText.getText().toString();
        List<AttachmentData> attachmentDataList =
                new ArrayList<>(m_chatViewModel.getUploadingAttachmentList());

        if (text.isEmpty() && attachmentDataList.isEmpty()) {
            // todo: may be it needs to show something..

            return;
        }

        MessageCipherProcessor messageCipherProcessor =
                MessageCipherProcessor.getInstance(m_chatId);

        if (messageCipherProcessor != null) {
            Error cipheringError;

            if (!text.isEmpty()) {
                ObjectWrapper<Pair<Boolean, String>> processedSuccessFlagText = new ObjectWrapper<>();
                cipheringError = messageCipherProcessor.processText(
                        text, true, processedSuccessFlagText);

                if (cipheringError != null) {
                    ErrorBroadcastReceiver.broadcastError(cipheringError,
                            m_context.getApplicationContext());

                    return;
                }

                text = processedSuccessFlagText.getValue().second;
            }

            if (!attachmentDataList.isEmpty()) {
                List<AttachmentData> processedAttachmentDataList = new ArrayList<>();

                for (final AttachmentData attachmentData : attachmentDataList) {
                    ObjectWrapper<AttachmentData> processedAttachmentData = new ObjectWrapper<>();
                    cipheringError =
                            messageCipherProcessor.cipherAttachmentData(
                                    m_context.getContentResolver(),
                                    attachmentData,
                                    processedAttachmentData);

                    if (cipheringError != null) {
                        ErrorBroadcastReceiver.broadcastError(cipheringError,
                                m_context.getApplicationContext());

                        return;
                    }

                    processedAttachmentDataList.add(processedAttachmentData.getValue());
                }

                attachmentDataList = processedAttachmentDataList;
            }
        }

        MessageToSendData messageToSendData =
                MessageToSendData.getInstance(
                        m_chatId,
                        text,
                        attachmentDataList);

        if (messageToSendData == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Message to send can't be generated!", true),
                            m_context.getApplicationContext());

            return;
        }

        m_sendingMessageText.getText().clear();
        m_chatViewModel.setUploadingAttachmentDataList(new ArrayList<>());

        processAttachmentListChange();

        m_messageSender.execute(messageToSendData);
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
                .broadcastError(error, m_context.getApplicationContext());
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
                .broadcastError(error, m_context.getApplicationContext());
    }

    @Override
    public void onAttachmentFilesPicked(
            final List<AttachmentData> pickedFileUriList)
    {
        if (pickedFileUriList == null) {
            processAttachmentListChange();

            return;
        }

        List<AttachmentData> chosenAttachmentDataList =
                m_chatViewModel.getUploadingAttachmentList();

        for (final AttachmentData attachmentData : pickedFileUriList) {
            if (chosenAttachmentDataList.contains(attachmentData)) continue;

            m_chatViewModel.addUploadingAttachmentData(attachmentData);
        }

        processAttachmentListChange();
    }

    @Override
    public void onRequestAnswerDialogResultGotten(
            final RequestAnswer requestAnswer)
    {
        Intent intent =
                new Intent(CommandProcessorServiceBroadcastReceiver.C_PROVIDE_REQUEST_ANSWER);

        intent.putExtra(
                CommandProcessorServiceBroadcastReceiver.C_REQUEST_ANSWER_PROP_NAME, requestAnswer);

        LocalBroadcastManager.
                getInstance(m_context.getApplicationContext()).
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

    @Override
    public void onAttachmentClicked(
            final AttachmentData attachmentData)
    {
        if (!m_chatViewModel.removeUploadingAttachmentData(attachmentData)) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Attachment Removing has been failed!", true),
                    m_context.getApplicationContext());

            return;
        }

        processAttachmentListChange();
    }

    @Override
    public AttachmentData getAttachmentByIndex(final int index) {
        return m_chatViewModel.getUploadingAttachmentDataByIndex(index);
    }

    @Override
    public int getAttachmentListSize() {
        return m_chatViewModel.getUploadingAttachmentList().size();
    }

    private void processAttachmentListChange() {
        if (!m_chatViewModel.getUploadingAttachmentList().isEmpty()) {
            m_attachmentListView.setVisibility(View.VISIBLE);
        } else
            m_attachmentListView.setVisibility(View.GONE);

        m_attachmentListAdapter.onDataSetChanged();
    }
}
