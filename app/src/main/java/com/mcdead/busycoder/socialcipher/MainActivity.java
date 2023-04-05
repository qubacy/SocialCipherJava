package com.mcdead.busycoder.socialcipher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.dialoglist.DialogsActivity;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorActivity;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.error.ErrorReceivedInterface;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorFactory;
import com.mcdead.busycoder.socialcipher.messageprocessor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.setting.manager.SettingsManager;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.signin.SignInWebViewActivity;
import com.mcdead.busycoder.socialcipher.signin.tokenchecker.TokenCheckResultInterface;
import com.mcdead.busycoder.socialcipher.signin.tokenchecker.TokenCheckerBase;
import com.mcdead.busycoder.socialcipher.signin.tokenchecker.TokenCheckerFactory;

// Initial activity;
// It has a mission to check whether the user is Signed In or not;


public class MainActivity extends AppCompatActivity
    implements TokenCheckResultInterface, ErrorReceivedInterface
{
    public static final String C_IS_CLOSING_EXTRA_PROP_NAME = "isClosing";

    private ErrorBroadcastReceiver m_errorBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIsClosing();

        setContentView(R.layout.activity_main);

        m_errorBroadcastReceiver = new ErrorBroadcastReceiver(this);

        if (!SettingsManager.initializeSettings(getFilesDir().getAbsolutePath())) {
            Toast.makeText(this, "Settings loading error!", Toast.LENGTH_LONG)
                    .show();
        }

        APIStore.init();

        String token = SettingsNetwork.getInstance().getToken();

        launchTokenCheck(token);

        findViewById(R.id.signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSignIn();
            }
        });

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        m_errorBroadcastReceiver,
                        new IntentFilter(ErrorBroadcastReceiver.C_ERROR_RECEIVED));
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

    private void checkIsClosing() {
        if (getIntent() == null) return;

        boolean isClosing = getIntent().getBooleanExtra(C_IS_CLOSING_EXTRA_PROP_NAME, false);

        if (isClosing) {
            finish();

            System.exit(-1);
        }
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
        Intent intent = new Intent(this, SignInWebViewActivity.class);

        startActivity(intent);
    }

    private void showDialogs() {
        Intent intent = new Intent(this, DialogsActivity.class);

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