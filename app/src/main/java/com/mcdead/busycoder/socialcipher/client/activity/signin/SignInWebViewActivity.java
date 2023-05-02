package com.mcdead.busycoder.socialcipher.client.activity.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.main.MainActivity;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.signin.data.SignInData;
import com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.SignInTokenFragment;
import com.mcdead.busycoder.socialcipher.client.activity.signin.fragment.SignInWebViewFragment;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class SignInWebViewActivity extends AppCompatActivity
    implements SignInCallback
{
    private LoginMode m_curLoginMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_web_view);

        m_curLoginMode = LoginMode.LOGIN_DATA;

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        Button switchButton = findViewById(R.id.signin_switch_login_type_button);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginMode loginMode = null;

                if (m_curLoginMode == LoginMode.LOGIN_DATA)
                    loginMode = LoginMode.TOKEN;
                else
                    loginMode = LoginMode.LOGIN_DATA;

                onSwitchLoginModeClicked(loginMode);
            }
        });

        if (getSupportFragmentManager().findFragmentById(R.id.signin_login_frame) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.signin_login_frame, new SignInWebViewFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void onSwitchLoginModeClicked(final LoginMode loginMode) {
        m_curLoginMode = loginMode;

        Fragment fragmentToSet = null;

        switch (m_curLoginMode) {
            case TOKEN: fragmentToSet = new SignInTokenFragment(this); break;
            case LOGIN_DATA: fragmentToSet = new SignInWebViewFragment(); break;
        }

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.signin_login_frame, fragmentToSet).
                commit();
    }

    @Override
    public void processError(final Error error) {
        ErrorBroadcastReceiver.broadcastError(error, getApplicationContext());
    }

    @Override
    public void processData(final SignInData data) {
        Log.d(getClass().getName(), "Got SignInResult: " + data.getToken());

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        settingsNetwork.setToken(data.getToken());
        new Thread(new SettingsSaver(settingsNetwork)).start();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.C_IS_SIGNED_IN_PROP_NAME, true);

        startActivity(intent);
    }

    public static class SettingsSaver implements Runnable {
        private SettingsNetwork m_settings = null;

        public SettingsSaver(final SettingsNetwork settings) {
            m_settings = settings;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            m_settings.store();
        }
    }

    private static enum LoginMode {
        LOGIN_DATA,
        TOKEN;
    }
}