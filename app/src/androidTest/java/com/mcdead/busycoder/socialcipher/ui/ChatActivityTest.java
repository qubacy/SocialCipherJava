package com.mcdead.busycoder.socialcipher.ui;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mcdead.busycoder.socialcipher.client.activity.chat.ChatActivity;
import com.mcdead.busycoder.socialcipher.client.api.APIType;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ChatActivityTest {
    private static final String C_VALID_TOKEN =
            "";
    private ActivityScenarioRule<ChatActivity> m_chatScenarioRule = null;

    @Before
    public void setUp() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        settingsNetwork.setAPIType(APIType.VK);
        settingsNetwork.setToken(C_VALID_TOKEN);

        ChatEntity chatEntity = ChatEntityGenerator.generateChatByType();

        ChatsStore chatsStore = ChatsStore.getInstance();

        chatsStore.addChat();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent chatIntent = new Intent(context, ChatActivity.class);

        m_chatScenarioRule = new ActivityScenarioRule<>(chatIntent);
    }

    @Test
    public void sendMessageTest() {

    }
}
