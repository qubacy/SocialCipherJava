package com.mcdead.busycoder.socialcipher.dialoglist;

import static com.mcdead.busycoder.socialcipher.updateprocessor.UpdateProcessorService.C_OPERATION_ID_PROP_NAME;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.loadingscreen.LoadingPopUpWindow;
import com.mcdead.busycoder.socialcipher.updateprocessor.UpdateProcessorService;
import com.mcdead.busycoder.socialcipher.R;

public class DialogsActivity extends AppCompatActivity
    implements DialogsListFragmentCallback
{
    private LoadingPopUpWindow m_loadingPopUpWindow = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialogs);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(R.string.chat_list_action_bar_title);
        }

        if (getSupportFragmentManager().findFragmentById(R.id.dialogs_list_fragment_frame) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.dialogs_list_fragment_frame, new DialogsListFragment(this))
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, UpdateProcessorService.class);

        intent.putExtra(C_OPERATION_ID_PROP_NAME, UpdateProcessorService.OperationType.START_UPDATE_CHECKER.getId());

        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        m_loadingPopUpWindow
                = LoadingPopUpWindow.generatePopUpWindow(this, getLayoutInflater());

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.show(findViewById(android.R.id.content).getRootView());
    }

    @Override
    public void onDialogsLoaded() {
        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.dismiss();
    }
}
