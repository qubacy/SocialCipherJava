package com.mcdead.busycoder.socialcipher;

import static org.junit.Assert.assertEquals;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentItem;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorFactory;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class MessageProcessorVKTest {
    private static final String C_VALID_TOKEN =
            "vk1.a.LzIN0oJe7pfK4pbl1-0vyDh_yKtgEfGPRmZlZ1LPzCd8RgC_Wp4GhrmLE0gaLouqSx9c4FJHm1c1QD2QLFZYb0cGeUXySP5Tu5-qAMDXqxzTeXmGKizv57zA7Y9Dabsuu7OUsSfankCD78O-e_BbUy4I9QWVkS6odCArRxR3FbKqM8Rlj_5CnKIdipOZyE2c";

    private MessageProcessorVK m_messageProcessor = null;

    @Before
    public void setUp() {
        SettingsNetwork.getInstance().setDefaults();

        AttachmentTypeDefinerInterface attachmentTypeDefiner = new AttachmentTypeDefinerVK();

        m_messageProcessor =
                (MessageProcessorVK) MessageProcessorFactory.
                        generateMessageProcessorVK(attachmentTypeDefiner, C_VALID_TOKEN);
    }

    @Test
    public void processingMessagesFromLoadingMatrix() {
        List<MessageFromLoadingTestData> messageFromLoadingList =
                new ArrayList<MessageFromLoadingTestData>()
                {
                    {
                        add(new MessageFromLoadingTestData(
                                ,
                                ,
                                ,
                                ,

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
                            messageTestData.resultMessageWrapper);

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
}
