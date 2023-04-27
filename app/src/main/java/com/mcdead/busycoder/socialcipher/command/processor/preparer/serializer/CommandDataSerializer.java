package com.mcdead.busycoder.socialcipher.command.processor.preparer.serializer;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class CommandDataSerializer {
    public static Error serializeCommandData(
            final CommandData commandData,
            ObjectWrapper<String> serializedCommandDataWrapper)
    {
        if (commandData == null || serializedCommandDataWrapper == null)
            return new Error("Serialization input was incorrect!", true);

        StringBuilder serializedCommandData =
                new StringBuilder(CommandContext.C_COMMAND_BEGINNING_SYMBOLS);

        serializedCommandData.append(String.valueOf(commandData.getCategory().getId()));
        serializedCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        serializedCommandData.append(String.valueOf(commandData.getChatId()));
        serializedCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        ObjectWrapper<String> serializedPeerIdListWrapper = new ObjectWrapper<>();
        Error error = serializeCommandPeerIdList(
                commandData.getReceiverPeerIdList(),
                serializedPeerIdListWrapper);

        if (error != null) return error;

        serializedCommandData.append(CommandContext.C_PART_DIVIDER_CHAR);
        serializedCommandData.append(commandData.getSpecificCommandTypeData());

        serializedCommandDataWrapper.setValue(serializedCommandData.toString());

        return null;
    }

    private static Error serializeCommandPeerIdList(
            final List<Long> peerIdList,
            ObjectWrapper<String> serializedPeerIdListWrapper)
    {
        if (peerIdList == null) {
            serializedPeerIdListWrapper.setValue("");

            return null;
        }

        StringBuilder serializedPeerIdList = new StringBuilder();
        int peerIdListSize = peerIdList.size();

        for (int i = 0; i < peerIdListSize; ++i) {
            serializedPeerIdList.append(peerIdList.get(i));
            serializedPeerIdList.append(
                    (i  + 1 == peerIdListSize ?
                            "" :
                            CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));
        }

        serializedPeerIdListWrapper.setValue(serializedPeerIdList.toString());

        return null;
    }
}
