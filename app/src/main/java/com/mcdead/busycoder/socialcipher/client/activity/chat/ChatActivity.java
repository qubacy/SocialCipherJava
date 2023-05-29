package com.mcdead.busycoder.socialcipher.client.activity.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.intent.AttachmentPickerActivityCallback;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.intent.AttachmentPickerActivityContract;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.utility.chat.ChatTitleExtractor;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.loadingscreen.LoadingPopUpWindow;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

public class ChatActivity extends AppCompatActivity
    implements ChatFragmentCallback
{
    public static final String C_PEER_ID_EXTRA_PROP_NAME = "peerId";

    public static final String C_DEFAULT_CHAT_NAME = "My Chat";

    private ChatFragment m_chatFragment = null;

    private ActivityResultLauncher<Void> m_attachmentPickerLauncher = null;

    private LoadingPopUpWindow m_loadingPopUpWindow = null;
    private boolean m_isChatLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        ObjectWrapper<Long> peerIdWrapper = new ObjectWrapper<>();
        Error peerIdError = retrievePeerIdFromIntent(peerIdWrapper);

        if (peerIdError != null) {
            finishWithError(new Error("Intent was null!", true));

            return;
        }

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            ObjectWrapper<String> titleWrapper = new ObjectWrapper<>();

            Error error = getChatTitleByPeerId(peerIdWrapper.getValue(), titleWrapper);

            if (error != null)
                finishWithError(error);

            actionBar.setTitle(titleWrapper.getValue());
        }

        m_chatFragment = (ChatFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (m_chatFragment == null) {
            ChatFragment chatFragment =
                    ChatFragment.getInstance(peerIdWrapper.getValue(), this, this);

            if (chatFragment == null) {
                finishWithError(new Error("Chat Fragment can't be generated!", true));

                return;
            }

            m_chatFragment = chatFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, m_chatFragment)
                    .commit();
        }

        m_attachmentPickerLauncher =
                registerForActivityResult(
                    new AttachmentPickerActivityContract(),
                    new AttachmentPickerActivityCallback(m_chatFragment));
    }

    private Error retrievePeerIdFromIntent(ObjectWrapper<Long> peerIdWrapper) {
        Intent intent = getIntent();

        if (intent == null)
            return new Error("Intent was null!", true);

        long peerId = intent.getLongExtra(C_PEER_ID_EXTRA_PROP_NAME, 0);

        if (peerId == 0)
            return new Error("Peer Id was incorrect!", true);

        peerIdWrapper.setValue(peerId);

        return null;
    }

    private void finishWithError(final Error error) {
        ErrorBroadcastReceiver.broadcastError(error, getApplicationContext());
        finish();
    }

    private Error getChatTitleByPeerId(
            final long chatId,
            ObjectWrapper<String> chatTitleWrapper)
    {
        ChatsStore chatsStore = ChatsStore.getInstance();

        if (chatsStore == null)
            return new Error("Chats' Store hasn't been initialized!", true);

        ChatEntity chat = chatsStore.getChatById(chatId);

        if (chat == null)
            return new Error("Chat with provided Id hasn't been found!", true);

        String title = ChatTitleExtractor.getTitleByChat(chat);

        if (title == null)
            chatTitleWrapper.setValue(C_DEFAULT_CHAT_NAME);
        else
            chatTitleWrapper.setValue(title);

        return null;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (m_isChatLoaded) return;

        m_loadingPopUpWindow
                = LoadingPopUpWindow.generatePopUpWindow(this, getLayoutInflater());

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.show(findViewById(android.R.id.content).getRootView());
    }

    @Override
    public void onDialogLoaded() {
        m_isChatLoaded = true;

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.dismiss();
    }

    @Override
    public void onAttachmentPickerDemanded() {
        m_attachmentPickerLauncher.launch(null);
    }
}
