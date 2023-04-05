package com.mcdead.busycoder.socialcipher.dialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.R;

public class DialogActivity extends AppCompatActivity {
    public static final String C_PEER_ID_EXTRA_PROP_NAME = "peerId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            Intent intent = getIntent();

            if (intent == null) {
                finish();

                return;
            }

            long peerId = intent.getLongExtra(C_PEER_ID_EXTRA_PROP_NAME, 0);

            if (peerId == 0) {
                finish();

                return;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new DialogFragment(peerId))
                    .commit();
        }
    }
}
