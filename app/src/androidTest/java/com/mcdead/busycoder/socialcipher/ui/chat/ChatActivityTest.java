package com.mcdead.busycoder.socialcipher.ui.chat;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mcdead.busycoder.socialcipher.client.activity.chat.ChatActivity;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.activity.chat.broadcastreceiver.ChatBroadcastReceiverCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragment;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.ChatFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist.MessageListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.APIType;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderVK;
import com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoadingCallback;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSendingCallback;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ChatActivityTest {
    private static final String C_VALID_TOKEN =
            "vk1.a.39k26RE5IJvADQdui5-Sg5IlUfc_i7IwdvWb9Ax3qm2VZpAH2HSwTcQWBvQQWMFlPuhN3qhO3MD52llG2ytVc7PDRD2zPQPoixip7HREBqwwdo7PaFog7mxUHv08ajPTM2qvoTn9QzpsuN8kllx9cchUQ5A5MVfSkTC0_-_VZw9yIhOV0XBxCcekvuUgPbdrXADePDcgb3au-uEQTaeYRA";

    private long m_chatId = 0;
    private long m_localPeerId = 0;

    private Context m_context = null;

    private FragmentScenario<ChatFragment> m_chatScenario = null;


    @Before
    public void setUp() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        settingsNetwork.setAPIType(APIType.VK);
        settingsNetwork.setToken(C_VALID_TOKEN);

        m_chatId = 1;

        ChatEntity chatEntity = ChatEntityGenerator.generateChatByType(ChatType.DIALOG, m_chatId);

        ChatsStore chatsStore = ChatsStore.getInstance();

        chatsStore.addChat(chatEntity);

        m_localPeerId = 1;

        m_context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void sendMessageTest() {
        ChatFragmentCallback callback = new ChatFragmentCallback() {
            @Override
            public void onChatLoaded() {
                return;
            }

            @Override
            public void onAttachmentPickerDemanded() {
                return;
            }
        };

        com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoadingCallback chatLoadingCallback =
                new ChatLoadingCallback() {
                    @Override
                    public void onChatLoaded() {

                    }

                    @Override
                    public void onChatLoadingError(Error error) {

                    }
                };

        com.mcdead.busycoder.socialcipher.client.processor.chat.loader.ChatLoaderVK chatLoaderVK =
                (ChatLoaderVK) com.mcdead.busycoder.socialcipher.client.processor.chat.
                        loader.ChatLoaderFactory.generateChatLoader(m_chatId, chatLoadingCallback);

        com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncBase attachmentUploader =
                com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.AttachmentUploaderSyncFactory.generateAttachmentUploader(m_context.getContentResolver());

        com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSendingCallback messageSendingCallback =
                new MessageSendingCallback() {
                    @Override
                    public void onMessageSent() {

                    }

                    @Override
                    public void onMessageSendingError(Error error) {

                    }
                };

        com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderBase messageSenderBase =
                com.mcdead.busycoder.socialcipher.client.processor.chat.message.sender.MessageSenderFactory.generateMessageSender(attachmentUploader, messageSendingCallback, ContextCompat.getMainExecutor(m_context));

        ChatFragmentFactory chatFragmentFactory =
                new ChatFragmentFactory(
                        m_chatId, m_localPeerId, callback,
                        (ChatLoaderBase) chatLoaderVK, attachmentUploader, messageSenderBase);

        m_chatScenario =
                FragmentScenario.launchInContainer(
                        ChatFragment.class, null, chatFragmentFactory);

        //m_chatScenario.moveToState(Lifecycle.State.STARTED);
    }
}
