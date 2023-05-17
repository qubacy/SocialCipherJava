package com.mcdead.busycoder.socialcipher.command.processor.preparer.parser;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandDataParser {
    private static final int C_COMMAND_MAIN_PART_COUNT = 1;
    private static final int C_COMMAND_HEADER_PART_COUNT = 2;

    public static final HashMap<ErrorType, Error> C_ERROR_HASH_MAP =
        new HashMap<ErrorType, Error>()
        {
            {
                put(ErrorType.INCORRECT_COMMAND,
                    new Error("Incorrect command has been provided!", true));
                put(ErrorType.INVALID_CATEGORY,
                    new Error("A command with an invalid category has been provided!", true));
            }
        };

    public static enum ErrorType {
        INCORRECT_COMMAND,
        INVALID_CATEGORY
    };

    public static Error parseCommandMessage(
            final CommandMessage commandMessage,
            ObjectWrapper<CommandData> commandDataWrapper)
    {
        if (commandMessage == null || commandDataWrapper == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_COMMAND);

        String commandText = commandMessage.getCommandString().substring(
                CommandContext.C_COMMAND_BEGINNING_SYMBOLS.length());
        String[] commandParts = commandText.split(CommandContext.C_PART_DIVIDER_CHAR);

        if (commandParts.length < C_COMMAND_MAIN_PART_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_COMMAND);

        String[] commandHeaderParts =
                commandParts[0].split(String.valueOf(CommandContext.C_SECTION_DIVIDER_CHAR));

        if (commandHeaderParts.length < C_COMMAND_HEADER_PART_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_COMMAND);

        ObjectWrapper<CommandCategory> commandCategoryWrapper =
                new ObjectWrapper<>();
        Error retrievingCategoryError = retrieveCommandCategory(
                commandHeaderParts[0],
                commandCategoryWrapper);

        if (retrievingCategoryError != null)
            return retrievingCategoryError;

        ObjectWrapper<List<Long>> receiverPeerIdListWrapper =
                new ObjectWrapper<>();
        Error retrievingReceiversError = retrieveReceiverPeerIdList(
                commandHeaderParts[1],
                receiverPeerIdListWrapper);

        if (retrievingReceiversError != null)
            return retrievingReceiversError;

        String commandBody =
                (commandParts.length > C_COMMAND_MAIN_PART_COUNT ?
                    commandParts[1] : "");

        CommandData commandData =
                new CommandData(
                        commandCategoryWrapper.getValue(),
                        commandMessage.getPeerId(),
                        receiverPeerIdListWrapper.getValue(),
                        commandBody);

        commandDataWrapper.setValue(commandData);

        return null;
    }

    private static Error retrieveCommandCategory(
            final String commandCategoryString,
            ObjectWrapper<CommandCategory> commandCategoryWrapper)
    {
        int commandCategoryId = Integer.parseInt(commandCategoryString);
        CommandCategory commandCategory =
                CommandCategory.getCategoryById(commandCategoryId);

        if (commandCategory == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INVALID_CATEGORY);

        commandCategoryWrapper.setValue(commandCategory);

        return null;
    }

    private static Error retrieveChatId(
            final String chatIdString,
            ObjectWrapper<Long> chatIdWrapper)
    {
        long chatId = Long.parseLong(chatIdString);

        chatIdWrapper.setValue(chatId);

        return null;
    }

    private static Error retrieveReceiverPeerIdList(
            final String retrieveReceiverPeerIdListString,
            ObjectWrapper<List<Long>> receiverPeerIdListWrapper)
    {
        String[] receiverPeerIdListParts =
                retrieveReceiverPeerIdListString.split(
                        String.valueOf(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

        if (receiverPeerIdListParts.length <= 0)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_COMMAND);
        if (receiverPeerIdListParts[0].compareTo(CommandContext.C_BROADCAST_SYMBOLS) == 0)
            return null;

        List<Long> receiverPeerIdList = new ArrayList<>();

        for (final String receiverPeerIdListPart : receiverPeerIdListParts) {
            long receiverPeerId = Long.parseLong(receiverPeerIdListPart);

            receiverPeerIdList.add(receiverPeerId);
        }

        receiverPeerIdListWrapper.setValue(receiverPeerIdList);

        return null;
    }
}
