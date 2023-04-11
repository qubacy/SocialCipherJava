package com.mcdead.busycoder.socialcipher.signin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import com.mcdead.busycoder.socialcipher.MainActivity;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class SignInWebViewActivity extends AppCompatActivity
    implements SignInCallback
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_web_view);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        if (getSupportFragmentManager().findFragmentById(R.id.signin_web_view_frame) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.signin_web_view_frame, new SignInWebViewFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void processError(Error error) {

    }

    @Override
    public void processData(SignInData data) {
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
}