package com.mcdead.busycoder.socialcipher.client.activity.chatlist;

import static com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService.C_OPERATION_ID_PROP_NAME;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.ChatListFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.ChatListFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.loadingscreen.LoadingPopUpWindow;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorService;

public class ChatListActivity extends AppCompatActivity
    implements ChatListFragmentCallback
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
                    .add(R.id.dialogs_list_fragment_frame, new ChatListFragment(this))
                    .commit();
        }

        Error startingError = startServices();

        if (startingError != null) {
            ErrorBroadcastReceiver.broadcastError(startingError, getApplicationContext());

            return;
        }
    }

    private Error startServices() {
        Intent updateProcessorIntent =
                new Intent(this, UpdateProcessorService.class);

        updateProcessorIntent.putExtra(
                C_OPERATION_ID_PROP_NAME,
                UpdateProcessorService.OperationType.START_UPDATE_CHECKER.getId());

        startService(updateProcessorIntent);

        Intent commandServiceIntent =
                new Intent(this, CommandProcessorService.class);
        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null)
            return new Error("Users Store hasn't been initialized!", true);

        UserEntity localUser = usersStore.getLocalUser();

        if (localUser == null)
            new Error("Local User hasn't been initialized!", true);

        commandServiceIntent.putExtra(
                CommandProcessorService.C_OPERATION_ID_PROP_NAME,
                CommandProcessorService.OperationType.INIT_SERVICE.getId());
        commandServiceIntent.putExtra(
                CommandProcessorService.C_LOCAL_USER_ID_PROP_NAME,
                localUser.getPeerId());

        startService(commandServiceIntent);

        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UpdateProcessorService.class));
        stopService(new Intent(this, CommandProcessorService.class));

        super.onDestroy();
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
