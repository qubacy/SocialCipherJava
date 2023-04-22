package com.mcdead.busycoder.socialcipher.activity.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.store.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.activity.chatlist.ChatListActivity;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.activity.error.ErrorActivity;
import com.mcdead.busycoder.socialcipher.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.activity.error.broadcastreceiver.ErrorReceivedInterface;
import com.mcdead.busycoder.socialcipher.processor.chat.message.processor.MessageProcessorFactory;
import com.mcdead.busycoder.socialcipher.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.setting.manager.SettingsManager;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.activity.signin.SignInWebViewActivity;
import com.mcdead.busycoder.socialcipher.processor.network.tokenchecker.result.TokenCheckResultInterface;
import com.mcdead.busycoder.socialcipher.processor.tokenchecker.TokenCheckerBase;
import com.mcdead.busycoder.socialcipher.processor.tokenchecker.TokenCheckerFactory;

// Initial activity;
// It has a mission to check whether the user is Signed In or not;

public class MainActivity extends AppCompatActivity
    implements
        TokenCheckResultInterface,
        ErrorReceivedInterface
{
    public static final String C_IS_CLOSING_EXTRA_PROP_NAME = "isClosing";
    public static final String C_IS_SIGNED_IN_PROP_NAME = "isSignedIn";

    private boolean m_isClosing = false;
    private boolean m_isSignedIn = false;

    private ErrorBroadcastReceiver m_errorBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            m_isClosing = getIntent().getBooleanExtra(C_IS_CLOSING_EXTRA_PROP_NAME, false);
            m_isSignedIn = getIntent().getBooleanExtra(C_IS_SIGNED_IN_PROP_NAME, false);
        }

        checkIsClosing();

        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        m_errorBroadcastReceiver = new ErrorBroadcastReceiver(this);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        m_errorBroadcastReceiver,
                        new IntentFilter(ErrorBroadcastReceiver.C_ERROR_RECEIVED));

        if (!SettingsNetwork.getInstance().isFullyInitialized()) {
            Error initError = init();

            if (initError != null) processError(initError);
        }

        if (m_isSignedIn) {
            String token = SettingsNetwork.getInstance().getToken();

            launchTokenCheck(token);
        }

        findViewById(R.id.main_activity_sign_in_vk_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSignIn();
            }
        });
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(m_errorBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private Error init() {
        if (!SettingsManager.initializeSettings(getFilesDir().getAbsolutePath())) {
            Toast.makeText(this, "Settings loading error!", Toast.LENGTH_LONG)
                    .show();
        }

        String token = SettingsNetwork.getInstance().getToken();

        launchTokenCheck(token);

        return null;
    }

    private void checkIsClosing() {
        if (!m_isClosing) return;

        finishAndRemoveTask();
    }

    private void launchTokenCheck(final String token) {
        if (token == null) return;
        if (token.isEmpty()) return;

        TokenCheckerBase tokenChecker = TokenCheckerFactory.generateTokenChecker(this);

        if (tokenChecker != null)
            tokenChecker.execute();
        else
            processError(
                    new Error("TokenCheckerFactory couldn't produce a checker object!",
                            true));
    }

    private void processSignIn() {
        Error error = resetStorages();

        if (error != null) {
            processError(error);

            return;
        }

        Intent intent = new Intent(this, SignInWebViewActivity.class);

        startActivity(intent);
    }

    private Error resetStorages() {
        if (ChatsStore.getInstance() == null
         || AttachmentsStore.getInstance() == null
         || UsersStore.getInstance() == null)
        {
            return new Error("Stores haven't been initialized!", true);
        }

        ChatsStore.getInstance().clean();
        AttachmentsStore.getInstance().clean();
        UsersStore.getInstance().clean();

        return null;
    }

    private void showDialogs() {
        Intent intent = new Intent(this, ChatListActivity.class);

        startActivity(intent);
    }

    @Override
    public void onTokenCheckSuccess(UserEntity localUser) {
        UsersStore.getInstance().setLocalUser(localUser);

        if (!MessageProcessorStore.init(MessageProcessorFactory.generateMessageProcessor())) {
            processError(new Error("MessageProcessor has not been initialized!", true));

            return;
        }

        showDialogs();
    }

    @Override
    public void onTokenCheckFailure(Error error) {
        processError(error);
    }

    @Override
    public void processError(final Error error) {
        if (error.isCritical()) {
            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);

            intent.putExtra(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME, error);

            startActivity(intent);

        } else {
            Log.d(getClass().getName(), "Error: " + error.getMessage());
        }
    }
}