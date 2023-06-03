package com.mcdead.busycoder.socialcipher.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatType;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.ChatsStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ChatsStoreTest {
    private static final long C_GETTING_TIMEOUT = 1000;

    private ChatsStore m_chatsStore = null;
    private ChatEntity m_chatEntity = null;

    @Before
    public void setUp() {
        m_chatsStore = ChatsStore.getInstance();
        m_chatEntity = ChatEntityGenerator.generateChatByType(ChatType.DIALOG, 123);

        m_chatsStore.addChat(m_chatEntity);
    }

    @After
    public void reset() {
        m_chatsStore.clean();
    }

    private void runWorkersSimultaneouslyAndWaitThem(Thread... threads) {
        for (final Thread thread : threads) {
            thread.start();
        }

        try {
            for (final Thread thread : threads) {
                thread.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void simultaneousChatGettingAndRemoving() {
        Runnable chatGetter = new Runnable() {
            @Override
            public void run() {
                ChatEntity chatEntity =
                        m_chatsStore.getChatById(m_chatEntity.getId());

                assertNotNull(chatEntity);
            }
        };
        Runnable chatRemover = new Runnable() {
            @Override
            public void run() {
                boolean removingResult =
                        m_chatsStore.removeChat(m_chatEntity.getId());

                assertTrue(removingResult);
            }
        };

        Thread getterThread = new Thread(chatGetter);
        Thread removerThread = new Thread(chatRemover);

        runWorkersSimultaneouslyAndWaitThem(getterThread, removerThread);
    }

    @Test
    public void removingChatThenGetting() {
        Runnable chatGetter = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(C_GETTING_TIMEOUT);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail();

                    return;
                }

                ChatEntity chatEntity =
                        m_chatsStore.getChatById(m_chatEntity.getId());

                assertNull(chatEntity);
            }
        };
        Runnable chatRemover = new Runnable() {
            @Override
            public void run() {
                boolean removingResult =
                        m_chatsStore.removeChat(m_chatEntity.getId());

                assertTrue(removingResult);
            }
        };

        Thread getterThread = new Thread(chatGetter);
        Thread removerThread = new Thread(chatRemover);

        runWorkersSimultaneouslyAndWaitThem(getterThread, removerThread);
    }

    @Test
    public void gettingChatListTwiceThenCheckingEquity() {
        List<ChatEntity> chatList1 = m_chatsStore.getChatList();
        List<ChatEntity> chatList2 = m_chatsStore.getChatList();

        assertEquals(chatList1, chatList2);
    }

    @Test
    public void addingMessageToChatThenRemoving() {
        long messageId = 123;
        UserEntity user =
                UserEntityGenerator.generateUserEntity(1, "somebody");

        MessageEntity messageEntity =
                MessageEntityGenerator.generateMessage(
                        messageId,
                        user,
                        "someText",
                        System.currentTimeMillis(),
                        false,
                        null);

        boolean addingMessageResult =
            m_chatsStore.addNewMessage(messageEntity, m_chatEntity.getId());

        assertTrue(addingMessageResult);

        MessageEntity gottenMessage = m_chatEntity.getMessageById(messageId);

        assertEquals(messageEntity, gottenMessage);

        boolean removingMessageResult =
            m_chatsStore.removeMessage(m_chatEntity.getId(), messageId);

        assertTrue(removingMessageResult);

        gottenMessage = m_chatEntity.getMessageById(messageId);

        assertNull(gottenMessage);
    }
}
