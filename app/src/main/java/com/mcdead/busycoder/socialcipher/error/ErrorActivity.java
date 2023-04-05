package com.mcdead.busycoder.socialcipher.error;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.R;

public class ErrorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_error);

        if (getIntent() == null) finish();

        Error error = (Error) getIntent().getSerializableExtra(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME);

        if (error == null) finish();

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new ErrorDialogFragment(error))
                    .commit();
        }
    }
}
