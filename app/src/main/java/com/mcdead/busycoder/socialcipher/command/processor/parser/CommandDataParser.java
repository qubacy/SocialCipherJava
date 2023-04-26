package com.mcdead.busycoder.socialcipher.command.processor.parser;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class CommandDataParser {
    private static final int C_COMMAND_PART_COUNT = 2;
    private static final int C_COMMAND_HEADER_PART_COUNT = 3;

    public static Error parseCommandMessage(
            final CommandMessage commandMessage,
            ObjectWrapper<CommandData> commandDataWrapper)
    {
        if (commandMessage == null || commandDataWrapper == null)
            return new Error("Incorrect command has been provided!", true);

        String commandText = commandMessage.getMessageEntity().getMessage();
        String[] commandParts =
                commandText.split(String.valueOf(CommandContext.C_PART_DIVIDER_CHAR));

        if (commandParts.length != C_COMMAND_PART_COUNT)
            return new Error("Incorrect command has been provided!", true);

        String[] commandHeaderParts =
                commandParts[0].split(String.valueOf(CommandContext.C_SECTION_DIVIDER_CHAR));

        if (commandHeaderParts.length < C_COMMAND_HEADER_PART_COUNT)
            return new Error("Incorrect command has been provided!", true);

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

        CommandData commandData =
                new CommandData(
                        commandCategoryWrapper.getValue(),
                        receiverPeerIdListWrapper.getValue(),
                        commandParts[1]);

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
            return new Error("A command with an invalid category has been provided!", true);

        commandCategoryWrapper.setValue(commandCategory);

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
            return new Error("Incorrect command has been provided!", true);
        if (receiverPeerIdListParts[0].isEmpty()) // case: broadcast;
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
