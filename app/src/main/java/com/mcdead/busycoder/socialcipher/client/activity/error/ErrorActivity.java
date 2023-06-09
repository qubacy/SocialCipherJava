package com.mcdead.busycoder.socialcipher.client.activity.error;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.client.activity.main.MainActivity;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.fragment.ErrorDialogFragment;
import com.mcdead.busycoder.socialcipher.client.activity.error.fragment.ErrorFragmentCallback;

public class ErrorActivity extends AppCompatActivity
    implements ErrorFragmentCallback
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_error);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        if (getIntent() == null) finish();

        Error error =
                (Error) getIntent().
                        getSerializableExtra(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME);

        if (error == null) finish();

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            ErrorDialogFragment errorDialogFragment =
                    ErrorDialogFragment.getInstance(error, this);

            if (errorDialogFragment == null) {
                ErrorBroadcastReceiver.broadcastError(
                        new Error(
                                "Error Dialog Fragment hasn't been initialized!",
                                true),
                        getApplicationContext());

                return;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, errorDialogFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        onErrorClosed();
    }

    @Override
    public void onErrorClosed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.C_IS_CLOSING_EXTRA_PROP_NAME, true);

        startActivity(intent);
    }
}
