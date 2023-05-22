package com.mcdead.busycoder.socialcipher;

import static org.junit.Assert.assertEquals;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.VKAttachmentType;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorFactory;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class MessageProcessorVKTest {
    private static final String C_VALID_TOKEN = "";
    private MessageProcessorVK m_messageProcessor = null;

    private long m_correctMessageId = 0;
    private long m_correctChatId = 0;
    private UserEntity m_correctSender = null;

    private long m_correctTimestamp = 0;
    private List<ResponseAttachmentInterface> m_correctAttachmentListAbstract = null;
    private List<ResponseAttachmentBase> m_correctAttachmentListVK = null;

    @Before
    public void setUp() {
        SettingsNetwork.getInstance().setDefaults();

        AttachmentTypeDefinerInterface attachmentTypeDefiner = new AttachmentTypeDefinerVK();
        ChatIdChecker chatIdChecker = ChatIdCheckerGenerator.generateChatIdChecker();

        m_messageProcessor =
                (MessageProcessorVK) MessageProcessorFactory.
                        generateMessageProcessorVK(attachmentTypeDefiner, C_VALID_TOKEN, chatIdChecker);

        m_correctMessageId = 123;
        m_correctChatId = 1;

        m_correctSender = UserEntityGenerator.generateUserEntity(1, "Somebody");

        m_correctTimestamp = System.currentTimeMillis();

        m_correctAttachmentListVK = new ArrayList<ResponseAttachmentBase>() {
            {
                add(new ResponseAttachmentStored(
                        VKAttachmentType.PHOTO.getType(),
                        124512612L,
                        634636734L));
            }
        };
        m_correctAttachmentListAbstract = new ArrayList<>();

        m_correctAttachmentListAbstract.addAll(m_correctAttachmentListVK);
    }

    @Test
    public void processingMessagesFromLoadingMatrix() {
        List<MessageFromLoadingTestData> messageFromLoadingList =
                new ArrayList<MessageFromLoadingTestData>()
                {
                    {
                        // CORRECT CASES:

                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "hi",
                                        null),
                                m_correctChatId,
                                m_correctSender,
                                new ObjectWrapper<MessageEntity>(
                                        MessageEntityGenerator.generateMessage(
                                                m_correctMessageId,
                                                m_correctSender,
                                                "hi",
                                                m_correctTimestamp,
                                                false,
                                                null
                                        )),
                                null
                        ));
                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        null,
                                        m_correctAttachmentListVK),
                                m_correctChatId,
                                m_correctSender,
                                new ObjectWrapper<MessageEntity>(
                                        MessageEntityGenerator.generateMessage(
                                                m_correctMessageId,
                                                m_correctSender,
                                                null,
                                                m_correctTimestamp,
                                                false,
                                                m_correctAttachmentListAbstract
                                        )),
                                null
                        ));
                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "",
                                        m_correctAttachmentListVK),
                                m_correctChatId,
                                m_correctSender,
                                new ObjectWrapper<MessageEntity>(
                                        MessageEntityGenerator.generateMessage(
                                                m_correctMessageId,
                                                m_correctSender,
                                                "",
                                                m_correctTimestamp,
                                                false,
                                                m_correctAttachmentListAbstract
                                        )),
                                null
                        ));
                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "",
                                        null),
                                m_correctChatId,
                                m_correctSender,
                                new ObjectWrapper<MessageEntity>(
                                        MessageEntityGenerator.generateMessage(
                                                m_correctMessageId,
                                                m_correctSender,
                                                "",
                                                m_correctTimestamp,
                                                false,
                                                null
                                        )),
                                null
                        ));

                        // INCORRECT CASES:

                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        null,
                                        null),
                                m_correctChatId,
                                m_correctSender,
                                new ObjectWrapper<>(),
                                MessageProcessorVK.C_ERROR_HASH_MAP.get(
                                        MessageProcessorVK.ErrorType.FAILED_MESSAGE_ENTITY_GENERATION)
                        ));
                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        0,
                                        m_correctTimestamp,
                                        "",
                                        null),
                                0,
                                m_correctSender,
                                new ObjectWrapper<>(),
                                MessageProcessorVK.C_ERROR_HASH_MAP.get(
                                        MessageProcessorVK.ErrorType.INVALID_CHAT_ID)
                        ));
                        add(new MessageFromLoadingTestData(
                                new ResponseChatContentItem(
                                        m_correctMessageId,
                                        m_correctSender.getPeerId(),
                                        0,
                                        m_correctTimestamp,
                                        "",
                                        null),
                                m_correctChatId,
                                null,
                                new ObjectWrapper<>(),
                                MessageProcessorVK.C_ERROR_HASH_MAP.get(
                                        MessageProcessorVK.ErrorType.NULL_LOADED_MESSAGE_SENDER)
                        ));
                    }
                };

        for (final MessageFromLoadingTestData messageTestData : messageFromLoadingList) {
            ObjectWrapper<MessageEntity> curResultMessageWrapper = new ObjectWrapper<>();
            Error curError =
                    m_messageProcessor.processReceivedMessage(
                            messageTestData.message,
                            messageTestData.peerId,
                            messageTestData.senderUser,
                            curResultMessageWrapper);

            assertEquals(messageTestData.error, curError);
            assertEquals(messageTestData.resultMessageWrapper, curResultMessageWrapper);
        }
    }

    @Test
    public void processingMessagesFromLoadingMatrix() {
        List<MessageFromLoadingTestData> messageFromUpdatingList =
                new ArrayList<MessageFromLoadingTestData>()
                {
                    {
                        // CORRECT CASES:



                        // INCORRECT CASES:


                    }
                };

        for (final MessageFromUpdatingTestData messageTestData : messageFromUpdatingList) {
            ObjectWrapper<MessageEntity> curResultMessageWrapper = new ObjectWrapper<>();
            Error curError =
                    m_messageProcessor.processReceivedUpdateMessage(
                            messageTestData.message,
                            messageTestData.peerId,
                            messageTestData.senderUser,
                            curResultMessageWrapper);

            assertEquals(messageTestData.error, curError);
            assertEquals(messageTestData.resultMessageWrapper, curResultMessageWrapper);
        }
    }

    private static class MessageFromLoadingTestData {
        final public ResponseChatContentItem message;
        final public long peerId;
        final public UserEntity senderUser;

        final public ObjectWrapper<MessageEntity> resultMessageWrapper;
        final public Error error;

        public MessageFromLoadingTestData(
                final ResponseChatContentItem message,
                final long peerId,
                final UserEntity senderUser,
                final ObjectWrapper<MessageEntity> resultMessageWrapper,
                final Error error)
        {
            this.message = message;
            this.peerId = peerId;
            this.senderUser = senderUser;
            this.resultMessageWrapper = resultMessageWrapper;
            this.error = error;
        }
    }

    private static class MessageFromUpdatingTestData {
        final public ResponseUpdateItem update;
        final public long peerId;
        final public UserEntity senderUser;

        final public ObjectWrapper<MessageEntity> resultMessageWrapper;
        final public Error error;

        public MessageFromUpdatingTestData(
                final ResponseUpdateItem update,
                final long peerId,
                final UserEntity senderUser,
                final ObjectWrapper<MessageEntity> resultMessageWrapper,
                final Error error)
        {
            this.update = update;
            this.peerId = peerId;
            this.senderUser = senderUser;
            this.resultMessageWrapper = resultMessageWrapper;
            this.error = error;
        }
    }
}
