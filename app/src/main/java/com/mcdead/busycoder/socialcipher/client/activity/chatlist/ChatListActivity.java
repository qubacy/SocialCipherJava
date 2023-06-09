package com.mcdead.busycoder.socialcipher.client.activity.chatlist;

import static com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService.C_OPERATION_ID_PROP_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.client.activity.chat.ChatActivity;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.ChatListFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.ChatListFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.loadingscreen.LoadingPopUpWindow;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.CacheCleanerBase;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.CacheCleanerCallback;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.CacheCleanerGenerator;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.cache.data.CacheCleanerResult;
import com.mcdead.busycoder.socialcipher.client.processor.update.service.UpdateProcessorService;
import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.command.processor.service.CommandProcessorService;

public class ChatListActivity extends AppCompatActivity
    implements
        ChatListFragmentCallback,
        CacheCleanerCallback
{
    private static final String C_IS_CHAT_LIST_LOADED_PROP_NAME = "isChatListLoaded";

    private boolean m_isChatListLoaded = false;

    private LoadingPopUpWindow m_loadingPopUpWindow = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chats);

        if (savedInstanceState != null) {
            m_isChatListLoaded = savedInstanceState.getBoolean(C_IS_CHAT_LIST_LOADED_PROP_NAME);
        }

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(R.string.chat_list_action_bar_title);
        }

        if (getSupportFragmentManager().findFragmentById(R.id.dialogs_list_fragment_frame) == null) {
            ChatListFragment chatListFragment =
                    ChatListFragment.getInstance(this, this);

            if (chatListFragment == null) {
                ErrorBroadcastReceiver.broadcastError(
                        new Error(
                            "Chat List Activity can't be initialized!", true),
                            getApplicationContext());

                return;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.dialogs_list_fragment_frame, chatListFragment)
                    .commit();
        }

        Error startingError = startServices();

        if (startingError != null) {
            ErrorBroadcastReceiver.broadcastError(startingError, getApplicationContext());

            return;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(C_IS_CHAT_LIST_LOADED_PROP_NAME, m_isChatListLoaded);
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

        if (m_isChatListLoaded) return;

        m_loadingPopUpWindow =
                LoadingPopUpWindow.generatePopUpWindow(this, getLayoutInflater());

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.show(findViewById(android.R.id.content).getRootView());
    }

    @Override
    public void onChatListLoaded() {
        m_isChatListLoaded = true;

        if (m_loadingPopUpWindow == null) return;

        m_loadingPopUpWindow.dismiss();
    }

    @Override
    public void onChatItemClicked(final long chatId) {
        Intent intent = new Intent(this, ChatActivity.class);

        intent.putExtra(ChatActivity.C_PEER_ID_EXTRA_PROP_NAME, chatId);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.chat_list_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_list_activity_menu_reset_cache:
                return onCacheResettingRequested();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean onCacheResettingRequested() {
        CacheCleanerBase cacheCleaner =
                CacheCleanerGenerator.generateCacheCleaner(this);

        if (cacheCleaner == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Cache Cleaner Generator hasn't generated the cleaner!", true),
                    getApplicationContext()
            );
        }

        cacheCleaner.execute();

        return true;
    }

    @Override
    public void onCacheCleanerErrorOccurred(
            final Error error)
    {
        ErrorBroadcastReceiver.broadcastError(error, getApplicationContext());
    }

    @Override
    public void onCacheCleanerResultGotten(
            final CacheCleanerResult cacheCleanerResult)
    {
        if (cacheCleanerResult.isSuccessful())
            Toast.makeText(this, "Cache has been reset!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Cache hasn't been reset!", Toast.LENGTH_LONG).show();
    }
}
