package com.mcdead.busycoder.socialcipher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.command.processor.preparer.parser.CommandDataParser;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CommandParsingTest {
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
    public void parsingCommandDataMatrix() {
        List<ParserTestData> parserTestDataList =
                new ArrayList<ParserTestData>() {
                    {
                        add(new ParserTestData(
                            generateCommandMessage(
                                CommandCategory.CIPHER,
                                null,
                                1,
                                1,
                                123,
                                ""),
                                null,
                                new ObjectWrapper<CommandData>(
                                    new CommandData(
                                        CommandCategory.CIPHER,
                                        1,
                                        null,
                                        "")
                                )));

                        List<Long> peerList = new ArrayList<Long>(){{add(2L); add(3L); add(4L);}};
                        add(new ParserTestData(
                            generateCommandMessage(
                                CommandCategory.CIPHER,
                                peerList,
                                1,
                                1,
                                123,
                                ""),
                                null,
                                new ObjectWrapper<CommandData>(
                                    new CommandData(
                                        CommandCategory.CIPHER,
                                        1,
                                        peerList,
                                        "")
                                )));

                        String specificData = "some specific commandData";
                        add(new ParserTestData(
                            generateCommandMessage(
                                CommandCategory.CIPHER,
                                null,
                                1,
                                1,
                                123,
                                specificData),
                                null,
                                new ObjectWrapper<CommandData>(
                                    new CommandData(
                                        CommandCategory.CIPHER,
                                        1,
                                        null,
                                        specificData)
                                )));

                        add(new ParserTestData(
                                generateCommandMessage(
                                    1,
                                    1,
                                    123,
                                    ""),
                                CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                                new ObjectWrapper<>()));
                        add(new ParserTestData(
                                generateCommandMessage(
                                        1,
                                        1,
                                        123,
                                        new StringBuilder().
                                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                                toString()),
                                CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                                new ObjectWrapper<>()));
                        add(new ParserTestData(
                                generateCommandMessage(
                                        1,
                                        1,
                                        123,
                                        new StringBuilder().
                                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                                append(String.valueOf(0)).
                                                toString()),
                                CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                                new ObjectWrapper<>()));
                        add(new ParserTestData(
                                generateCommandMessage(
                                        1,
                                        1,
                                        123,
                                        new StringBuilder().
                                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                                append(String.valueOf(0)).
                                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                                append(CommandContext.C_BROADCAST_SYMBOLS).
                                                toString()),
                                CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INVALID_CATEGORY),
                                new ObjectWrapper<>()));
                        add(new ParserTestData(
                                generateCommandMessage(
                                        1,
                                        1,
                                        123,
                                        new StringBuilder().
                                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                                append(CommandCategory.CIPHER.getId()).
                                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                                toString()),
                                CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                                new ObjectWrapper<>()));
                        add(new ParserTestData(
                                generateCommandMessage(
                                        1,
                                        1,
                                        123,
                                        new StringBuilder().
                                                append(CommandContext.C_COMMAND_BEGINNING_SYMBOLS).
                                                append(CommandCategory.CIPHER.getId()).
                                                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                                append(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR).
                                                toString()),
                                CommandDataParser.C_ERROR_HASH_MAP.get(CommandDataParser.ErrorType.INCORRECT_COMMAND),
                                new ObjectWrapper<>()));
                        add(new ParserTestData(
                                generateCommandMessage(
                                        1,
                                        1,
                                        123,
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

        for (final ParserTestData parserTestData : parserTestDataList) {
            ObjectWrapper<CommandData> curCommandData = new ObjectWrapper<>();
            Error curError =
                    CommandDataParser.parseCommandMessage(
                        parserTestData.commandMessage,
                        curCommandData);

            assertEquals(parserTestData.error, curError);
            assertEquals(parserTestData.resultWrapper, curCommandData);
        }
    }

    private static class ParserTestData {
        final public CommandMessage commandMessage;
        final public Error error;
        final public ObjectWrapper<CommandData> resultWrapper;

        public ParserTestData(
                CommandMessage commandMessage)
        {
            this.commandMessage = commandMessage;
            this.error = null;
            this.resultWrapper = new ObjectWrapper<>();
        }

        public ParserTestData(
                CommandMessage commandMessage,
                Error error,
                ObjectWrapper<CommandData> resultWrapper)
        {
            this.commandMessage = commandMessage;
            this.error = error;
            this.resultWrapper = resultWrapper;
        }
    };
}

