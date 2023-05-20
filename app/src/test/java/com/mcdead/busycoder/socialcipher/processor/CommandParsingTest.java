package com.mcdead.busycoder.socialcipher.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.preparer.parser.CommandDataParser;
import com.mcdead.busycoder.socialcipher.command.processor.preparer.serializer.CommandDataSerializer;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CommandParsingTest {
    private List<Long> m_correctPeerIdList = null;

    private long m_correctChatId = 0;
    private long m_correctInitializerPeerId = 0;
    private long m_correctMessageId = 0;
    private String m_correctSpecificCommandData = null;
    private String m_correctFullSpecificCommandData = null;

    @Before
    public void prepareCommonData() {
        m_correctPeerIdList = new ArrayList<Long>(){{add(2L); add(3L); add(4L);}};

        m_correctChatId = 1;
        m_correctInitializerPeerId = 1;
        m_correctMessageId = 123;
        m_correctSpecificCommandData = "";
        m_correctFullSpecificCommandData = "some specific commandData";
    }

    private String generateCommandString(
            final CommandCategory commandCategory,
            final List<Long> receiverPeerIdList,
            final String specificCommandData)
    {
        StringBuilder commandString = new StringBuilder();

        commandString.
                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                append(commandCategory.getId()).
                append(CommandContext.C_SECTION_DIVIDER_CHAR);

        if (receiverPeerIdList == null)
            commandString.append(CommandContext.C_BROADCAST_SYMBOLS);
        else {
            int receiverPeerIdListSize = receiverPeerIdList.size();

            for (int i = 0; i < receiverPeerIdListSize; ++i) {
                commandString
                        .append(receiverPeerIdList.get(i))
                        .append((i + 1 == receiverPeerIdListSize ?
                                "" :
                                CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));
            }
        }

        if (specificCommandData != null) {
            commandString.
                    append(CommandContext.C_PART_DIVIDER_CHAR).
                    append(specificCommandData);
        }

        return commandString.toString();
    }

    private CommandMessage generateCommandMessage(
            final CommandCategory commandCategory,
            final List<Long> receiverPeerIdList,
            final long chatId,
            final long initializerPeerId,
            final long messageId,
            final String specificCommandData)
    {
        return CommandMessage.getInstance(
                chatId,
                initializerPeerId,
                messageId,
                generateCommandString(
                        commandCategory,
                        receiverPeerIdList,
                        specificCommandData));
    }

    private CommandMessage generateCommandMessage(
            final long chatId,
            final long initializerPeerId,
            final long messageId,
            final String commandString)
    {
        return CommandMessage.getInstance(
                chatId,
                initializerPeerId,
                messageId,
                commandString);
    }

    @Test
    public void serializingCommandDataMatrix() {
        List<CommandSerializerTestData> serializerTestDataList =
            new ArrayList<CommandSerializerTestData>() {
                {
                    // CORRECT CASES:

                    add(new CommandSerializerTestData(
                        CommandData.getInstance(
                            CommandCategory.CIPHER,
                            m_correctChatId,
                            m_correctPeerIdList,
                            m_correctSpecificCommandData),
                        null,
                        new ObjectWrapper<String>(
                            generateCommandString(
                                CommandCategory.CIPHER,
                                m_correctPeerIdList,
                                m_correctSpecificCommandData)
                        )
                    ));
                    add(new CommandSerializerTestData(
                        CommandData.getInstance(
                            CommandCategory.CIPHER,
                            m_correctChatId,
                            null,
                            m_correctSpecificCommandData),
                        null,
                        new ObjectWrapper<String>(
                            generateCommandString(
                                CommandCategory.CIPHER,
                                null,
                                m_correctSpecificCommandData)
                        )
                    ));
                    add(new CommandSerializerTestData(
                        CommandData.getInstance(
                            CommandCategory.CIPHER,
                            m_correctChatId,
                            null,
                            m_correctFullSpecificCommandData),
                        null,
                        new ObjectWrapper<String>(
                            generateCommandString(
                                CommandCategory.CIPHER,
                                null,
                                m_correctFullSpecificCommandData)
                        )
                    ));

                    // INCORRECT CASES:
                    // Not necessary. CommandData's correctness checking is provided during
                    // it's creating process.
                }
            };

        for (final CommandSerializerTestData commandSerializerTestData : serializerTestDataList) {
            ObjectWrapper<String> curSerializedCommandWrapper = new ObjectWrapper<>();
            Error curSerializingError =
                    CommandDataSerializer.
                            serializeCommandData(
                                    commandSerializerTestData.commandData,
                                    curSerializedCommandWrapper);

            assertEquals(commandSerializerTestData.error, curSerializingError);
            assertEquals(
                    commandSerializerTestData.serializedCommandDataWrapper,
                    curSerializedCommandWrapper);
        }
    }

    @Test
    public void parsingCommandDataMatrix() {
        List<CommandParserTestData> parserTestDataList =
            new ArrayList<CommandParserTestData>() {
                {
                    // CORRECT CASES:

                    add(new CommandParserTestData(
                        generateCommandMessage(
                            CommandCategory.CIPHER,
                            null,
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            m_correctSpecificCommandData),
                            null,
                            new ObjectWrapper<CommandData>(
                                CommandData.getInstance(
                                    CommandCategory.CIPHER,
                                    1,
                                    null,
                                    "")
                            )));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            CommandCategory.CIPHER,
                                m_correctPeerIdList,
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            m_correctSpecificCommandData),
                            null,
                            new ObjectWrapper<CommandData>(
                                CommandData.getInstance(
                                    CommandCategory.CIPHER,
                                    1,
                                        m_correctPeerIdList,
                                    "")
                            )));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            CommandCategory.CIPHER,
                            null,
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            m_correctFullSpecificCommandData),
                            null,
                            new ObjectWrapper<CommandData>(
                                CommandData.getInstance(
                                    CommandCategory.CIPHER,
                                    1,
                                    null,
                                    m_correctFullSpecificCommandData)
                            )));

                    // INCORRECT CASES:

                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            ""),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                        new ObjectWrapper<>()));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            new StringBuilder().
                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                toString()),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                        new ObjectWrapper<>()));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            new StringBuilder().
                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                append(String.valueOf(0)).
                                toString()),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                        new ObjectWrapper<>()));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            new StringBuilder().
                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                append(String.valueOf(0)).
                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                append(CommandContext.C_BROADCAST_SYMBOLS).
                                toString()),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INVALID_CATEGORY),
                        new ObjectWrapper<>()));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            new StringBuilder().
                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                append(CommandCategory.CIPHER.getId()).
                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                toString()),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                        new ObjectWrapper<>()));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            new StringBuilder().
                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                append(CommandCategory.CIPHER.getId()).
                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                append(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR).
                                toString()),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                        new ObjectWrapper<>()));
                    add(new CommandParserTestData(
                        generateCommandMessage(
                            m_correctChatId,
                            m_correctInitializerPeerId,
                            m_correctMessageId,
                            new StringBuilder().
                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                append(CommandCategory.CIPHER.getId()).
                                append(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR).
                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                toString()),
                        CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                        new ObjectWrapper<>()));
                }
            };

        for (final CommandParserTestData parserTestData : parserTestDataList) {
            ObjectWrapper<CommandData> curCommandData = new ObjectWrapper<>();
            Error curError =
                    CommandDataParser.parseCommandMessage(
                        parserTestData.commandMessage,
                        curCommandData);

            assertEquals(parserTestData.error, curError);
            assertEquals(parserTestData.resultWrapper, curCommandData);
        }
    }

    private static class CommandParserTestData {
        final public CommandMessage commandMessage;
        final public Error error;
        final public ObjectWrapper<CommandData> resultWrapper;

        public CommandParserTestData(
                CommandMessage commandMessage)
        {
            this.commandMessage = commandMessage;
            this.error = null;
            this.resultWrapper = new ObjectWrapper<>();
        }

        public CommandParserTestData(
                CommandMessage commandMessage,
                Error error,
                ObjectWrapper<CommandData> resultWrapper)
        {
            this.commandMessage = commandMessage;
            this.error = error;
            this.resultWrapper = resultWrapper;
        }
    };

    private static class CommandSerializerTestData {
        final public CommandData commandData;
        final public Error error;
        final public ObjectWrapper<String> serializedCommandDataWrapper;

        public CommandSerializerTestData(
                final CommandData commandData,
                final Error error,
                final ObjectWrapper<String> serializedCommandDataWrapper)
        {
            this.commandData = commandData;
            this.error = error;
            this.serializedCommandDataWrapper = serializedCommandDataWrapper;
        }
    }
}

