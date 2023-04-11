package com.mcdead.busycoder.socialcipher.dialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.DialogsStore;
import com.mcdead.busycoder.socialcipher.data.entity.DialogTitleExtractor;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

public class DialogActivity extends AppCompatActivity {
    public static final String C_PEER_ID_EXTRA_PROP_NAME = "peerId";

    public static final String C_DEFAULT_CHAT_NAME = "My Chat";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);

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

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new DialogFragment(peerIdWrapper.getValue()))
                    .commit();
        }
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
        DialogsStore dialogsStore = DialogsStore.getInstance();

        if (dialogsStore == null)
            return new Error("Dialogs Store hasn't been initialized!", true);

        DialogEntity chat = dialogsStore.getDialogByPeerId(peerId);

        if (chat == null)
            return new Error("Dialog with provided Peer Id hasn't been found!", true);

        String title = DialogTitleExtractor.getTitleByDialog(chat);

        if (title == null)
            chatTitleWrapper.setValue(C_DEFAULT_CHAT_NAME);
        else
            chatTitleWrapper.setValue(title);

        return null;
    }
}
