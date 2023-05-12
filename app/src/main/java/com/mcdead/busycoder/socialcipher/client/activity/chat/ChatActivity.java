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

    private ChatFragment m_dialogFragment = null;

    private ActivityResultLauncher<Void> m_attachmentPickerLauncher = null;

    private LoadingPopUpWindow m_loadingPopUpWindow = null;
    private boolean m_isDialogLoaded = false;

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

        m_dialogFragment = (ChatFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (m_dialogFragment == null) {
            m_dialogFragment = new ChatFragment(peerIdWrapper.getValue(), this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, m_dialogFragment)
                    .commit();
        }

        m_attachmentPickerLauncher =
                registerForActivityResult(
                    new AttachmentPickerActivityContract(),
                    new AttachmentPickerActivityCallback(m_dialogFragment));
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
            final long peerId,
            ObjectWrapper<String> chatTitleWrapper)
    {
        ChatsStore dialogsStore = ChatsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);

        ChatEntity chat = dialogsStore.getChatByPeerId(peerId);

        if (chat == null)
            return new Error("Dialog with provided Peer Id hasn't been found!", true);

        String title = ChatTitleExtractor.getTitleByDialog(chat);

        if (title == null)
            chatTitleWrapper.setValue(C_DEFAULT_CHAT_NAME);
        else
            chatTitleWrapper.setValue(title);

        return null;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (m_isDialogLoaded) return;

        m_loadingPopUpWindow
                = LoadingPopUpWindow.generatePopUpWindow(this, getLayoutInflater());

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.show(findViewById(android.R.id.content).getRootView());
    }

    @Override
    public void onDialogLoaded() {
        m_isDialogLoaded = true;

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.dismiss();
    }

    @Override
    public void onAttachmentPickerDemanded() {
        m_attachmentPickerLauncher.launch(null);
    }
}
