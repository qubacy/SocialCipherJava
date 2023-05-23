package com.mcdead.busycoder.socialcipher;

import static com.mcdead.busycoder.socialcipher.setting.manager.SettingsManager.C_ATTACHMENT_DIR_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.VKAttachmentType;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.AttachmentsStore;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.processor.data.AttachmentProcessingResult;
import com.mcdead.busycoder.socialcipher.client.processor.network.update.processor.UpdateProcessorAsyncVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorFactory;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(JUnit4.class)
public class MessageProcessorVKTest {
    private static final String C_VALID_TOKEN = "";
    private Context m_context = null;
    private MessageProcessorVK m_messageProcessor = null;

    private long m_correctMessageId = 0;
    private long m_correctChatId = 0;
    private UserEntity m_correctSender = null;

    private long m_correctTimestamp = 0;

    private ResponseAttachmentStored m_responseAttachmentDoc = null;
    private ResponseAttachmentStored m_responseAttachmentPhoto = null;

    private List<ResponseAttachmentInterface> m_correctAttachmentListAbstract = null;
    private List<ResponseAttachmentBase> m_correctAttachmentListVK = null;

    private String generateAttachmentFilePath(
            final String dirPath,
            final String attachmentId,
            final AttachmentSize attachmentSize,
            final String attachmentExtension)
    {
        return (dirPath +
                '/' + String.valueOf(attachmentSize.getId()) +
                '/' + attachmentId +
                '.' + attachmentExtension);
    }

    @Before
    public void setUp() {
        m_context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String baseDir = m_context.getFilesDir().getAbsolutePath();

        SettingsSystem.init(baseDir, baseDir + '/' + C_ATTACHMENT_DIR_NAME, m_context);
        SettingsNetwork.getInstance().setDefaults();

        AttachmentTypeDefinerInterface attachmentTypeDefiner = new AttachmentTypeDefinerVK();
        ChatIdChecker chatIdChecker = ChatIdCheckerGenerator.generateChatIdChecker();

        m_messageProcessor =
                (MessageProcessorVK) MessageProcessorFactory.
                        generateMessageProcessorVK(attachmentTypeDefiner, C_VALID_TOKEN, chatIdChecker);

        m_correctChatId = 180106935;

        m_correctMessageId = 691929;
        m_correctSender = UserEntityGenerator.generateUserEntity(180106935, "Somebody");
        m_correctTimestamp = System.currentTimeMillis();

        m_responseAttachmentDoc =
                new ResponseAttachmentStored(
                    VKAttachmentType.DOC.getType(),
                    664209310,
                    180106935,
                    "");
        m_responseAttachmentPhoto =
                new ResponseAttachmentStored(
                        VKAttachmentType.PHOTO.getType(),
                        457252632,
                        180106935,
                        "");

        m_correctAttachmentListVK = new ArrayList<ResponseAttachmentBase>() {
            {
                add(m_responseAttachmentDoc);
                add(m_responseAttachmentPhoto);
            }
        };
        m_correctAttachmentListAbstract = new ArrayList<ResponseAttachmentInterface>() {
            {
                addAll(m_correctAttachmentListVK);
            }
        };
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
    public void processingMessagesFromUpdatingMatrix() {
        List<MessageFromUpdatingTestData> messageFromUpdatingList =
                new ArrayList<MessageFromUpdatingTestData>()
                {
                    {
                        // CORRECT CASES:

                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "hi",
                                        m_correctSender.getPeerId(),
                                        null
                                ),
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
                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        null,
                                        m_correctSender.getPeerId(),
                                        m_correctAttachmentListVK
                                ),
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
                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "",
                                        m_correctSender.getPeerId(),
                                        m_correctAttachmentListVK
                                ),
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
                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "",
                                        m_correctSender.getPeerId(),
                                        null
                                ),
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

                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        null,
                                        m_correctSender.getPeerId(),
                                        null
                                ),
                                m_correctChatId,
                                m_correctSender,
                                new ObjectWrapper<>(),
                                MessageProcessorVK.C_ERROR_HASH_MAP.get(
                                        MessageProcessorVK.ErrorType.FAILED_MESSAGE_ENTITY_GENERATION)
                        ));
                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        0,
                                        m_correctTimestamp,
                                        "",
                                        m_correctSender.getPeerId(),
                                        null
                                ),
                                0,
                                m_correctSender,
                                new ObjectWrapper<>(),
                                MessageProcessorVK.C_ERROR_HASH_MAP.get(
                                        MessageProcessorVK.ErrorType.INVALID_CHAT_ID)
                        ));
                        add(new MessageFromUpdatingTestData(
                                new ResponseUpdateItem(
                                        UpdateProcessorAsyncVK.EventType.NEW_MESSAGE.getEventId(),
                                        m_correctMessageId,
                                        0,
                                        m_correctChatId,
                                        m_correctTimestamp,
                                        "",
                                        0,
                                        null
                                ),
                                m_correctChatId,
                                null,
                                new ObjectWrapper<>(),
                                MessageProcessorVK.C_ERROR_HASH_MAP.get(
                                        MessageProcessorVK.ErrorType.NULL_UPDATE_MESSAGE_SENDER)
                        ));
                    }
                };

        for (final MessageFromUpdatingTestData messageTestData : messageFromUpdatingList) {
            ObjectWrapper<MessageEntity> curResultMessageWrapper = new ObjectWrapper<>();
            Error curError =
                    m_messageProcessor.processReceivedUpdateMessage(
                            messageTestData.update,
                            messageTestData.peerId,
                            messageTestData.senderUser,
                            curResultMessageWrapper);

            assertEquals(messageTestData.error, curError);
            assertEquals(messageTestData.resultMessageWrapper, curResultMessageWrapper);
        }
    }

    @Test
    public void processingAttachmentsMatrix() {
        assertNotNull(AttachmentsStore.getInstance());

        String attachmentDir = SettingsSystem.getInstance().getAttachmentsDir();

        HashMap<AttachmentSize, String> attachmentDocSizeFilePathHashMap =
                new HashMap<>();

        attachmentDocSizeFilePathHashMap.put(
                AttachmentSize.STANDARD,
                generateAttachmentFilePath(
                        attachmentDir,
                        m_responseAttachmentDoc.getTypedShortAttachmentId(),
                        AttachmentSize.STANDARD,
                        "txt"));

        HashMap<AttachmentSize, String> attachmentPhotoSizeFilePathHashMap =
                new HashMap<>();

        attachmentPhotoSizeFilePathHashMap.put(
                AttachmentSize.STANDARD,
                generateAttachmentFilePath(
                        attachmentDir,
                        m_responseAttachmentPhoto.getTypedShortAttachmentId(),
                        AttachmentSize.STANDARD,
                        "jpg"));
        attachmentPhotoSizeFilePathHashMap.put(
                AttachmentSize.SMALL,
                generateAttachmentFilePath(
                        attachmentDir,
                        m_responseAttachmentPhoto.getTypedShortAttachmentId(),
                        AttachmentSize.SMALL,
                        "jpg"));

        AttachmentEntityBase attachmentDocEntity =
                AttachmentEntityGenerator.
                        generateAttachmentByIdAndAttachmentSizeFilePathHashMap(
                                m_responseAttachmentDoc.getTypedShortAttachmentId(),
                                attachmentDocSizeFilePathHashMap);
        AttachmentEntityBase attachmentPhotoEntity =
                AttachmentEntityGenerator.
                        generateAttachmentByIdAndAttachmentSizeFilePathHashMap(
                                m_responseAttachmentPhoto.getTypedShortAttachmentId(),
                                attachmentPhotoSizeFilePathHashMap);

        List<AttachmentEntityBase> correctAttachmentEntityList =
                new ArrayList<AttachmentEntityBase>() {
                    {
                        add(attachmentDocEntity);
                        add(attachmentPhotoEntity);
                    }
                };

        List<AttachmentTestData> attachmentTestDataList = new ArrayList<AttachmentTestData>() {
            {
                // CORRECT CASES:

                add(new AttachmentTestData(
                        MessageEntityGenerator.generateMessage(
                                m_correctMessageId,
                                m_correctSender,
                                "hi",
                                m_correctTimestamp,
                                false,
                                m_correctAttachmentListAbstract),
                        m_correctChatId,
                        new ObjectWrapper<AttachmentProcessingResult>(
                                new AttachmentProcessingResult(
                                        correctAttachmentEntityList,
                                    false)),
                        null
                ));
                add(new AttachmentTestData(
                        MessageEntityGenerator.generateMessage(
                                m_correctMessageId,
                                m_correctSender,
                                "hi",
                                m_correctTimestamp,
                                false,
                                null),
                        m_correctChatId,
                        new ObjectWrapper<AttachmentProcessingResult>(),
                        null
                ));

                // INCORRECT CASES:
                // There isn't any reason to include simple cases;

                // todo: it would be nice to add some network-targeted failing tests with mock
                // todo: API providers..
            }
        };

        for (final AttachmentTestData attachmentTestData : attachmentTestDataList) {
            ObjectWrapper<AttachmentProcessingResult> attachmentProcessingResultWrapper =
                    new ObjectWrapper<>();
            Error curError =
                    m_messageProcessor.processMessageAttachments(
                            attachmentTestData.message,
                            attachmentTestData.chatId,
                            attachmentProcessingResultWrapper);

            assertEquals(attachmentTestData.error, curError);
            assertEquals(
                    attachmentTestData.resultAttachmentProcessing,
                    attachmentProcessingResultWrapper);
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

    private static class AttachmentTestData {
        final public MessageEntity message;
        final public long chatId;

        final public ObjectWrapper<AttachmentProcessingResult> resultAttachmentProcessing;
        final public Error error;

        public AttachmentTestData(
                final MessageEntity message,
                final long chatId,
                final ObjectWrapper<AttachmentProcessingResult> resultAttachmentProcessing,
                final Error error)
        {
            this.message = message;
            this.chatId = chatId;
            this.resultAttachmentProcessing = resultAttachmentProcessing;
            this.error = error;
        }
    }
}
