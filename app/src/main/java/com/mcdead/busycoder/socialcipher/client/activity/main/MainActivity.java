package com.mcdead.busycoder.socialcipher.client.activity.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.cipher.data.storage.CipherSessionStore;
import com.mcdead.busycoder.socialcipher.client.data.store.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.ChatListActivity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.ErrorActivity;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorReceivedInterface;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.setting.manager.SettingsManager;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInActivity;
import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResultInterface;
import com.mcdead.busycoder.socialcipher.client.processor.tokenchecker.TokenCheckerBase;
import com.mcdead.busycoder.socialcipher.client.processor.tokenchecker.TokenCheckerFactory;

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

    private Button m_vkButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            m_isClosing = getIntent().getBooleanExtra(C_IS_CLOSING_EXTRA_PROP_NAME, false);
            m_isSignedIn = getIntent().getBooleanExtra(C_IS_SIGNED_IN_PROP_NAME, false);
        }

        checkIsClosing();

        setContentView(R.layout.activity_main);

        m_vkButton = findViewById(R.id.main_activity_sign_in_vk_button);

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
            m_vkButton.setEnabled(false);

            String token = SettingsNetwork.getInstance().getToken();

            launchTokenCheck(token);
        }

        m_vkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSignIn();
            }
        });
    }

    @Override
    protected void onDestroy() {
//        new Thread(new SettingsSaver()).start();

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(m_errorBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setLoginButtonsEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private Error init() {
        Error loadingError =
                SettingsManager.initializeSettings(
                    getFilesDir().getAbsolutePath(), getApplicationContext());

        if (loadingError != null) processError(loadingError);

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

        Intent intent = new Intent(this, SignInActivity.class);

        startActivity(intent);
    }

    private Error resetStorages() {
        if (ChatsStore.getInstance() == null ||
            AttachmentsStore.getInstance() == null ||
            UsersStore.getInstance() == null ||
            CipherSessionStore.getInstance() == null)
        {
            return new Error("Stores haven't been initialized!", true);
        }

        ChatsStore.getInstance().clean();
        AttachmentsStore.getInstance().clean();
        UsersStore.getInstance().clean();
        CipherSessionStore.getInstance().clean();

        return null;
    }

    private void showChats() {
        Intent intent = new Intent(this, ChatListActivity.class);

        startActivity(intent);
    }

    private void setLoginButtonsEnabled(
            final boolean isEnabled)
    {
        m_vkButton.setEnabled(isEnabled);
    }

    @Override
    public void onTokenCheckSuccess(final UserEntity localUser) {
        UsersStore.getInstance().setLocalUser(localUser);

        if (!MessageProcessorStore.init(MessageProcessorFactory.generateMessageProcessor())) {
            processError(new Error("MessageProcessor has not been initialized!", true));

            return;
        }

        showChats();
    }

    @Override
    public void onTokenCheckFailure(final Error error) {
        processError(error);
    }

    @Override
    public void processError(final Error error) {
        if (error.isCritical()) {
            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);

            intent.putExtra(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME, error);

            startActivity(intent);

        } else {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(getClass().getName(), "Error: " + error.getMessage());
        }
    }

    public static class SettingsSaver implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            SettingsManager.saveSettings();
        }
    }
}