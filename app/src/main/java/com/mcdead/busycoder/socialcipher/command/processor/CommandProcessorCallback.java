package com.mcdead.busycoder.socialcipher.command.processor;

import com.mcdead.busycoder.socialcipher.command.CommandCategory;

import java.util.List;

public interface CommandProcessorCallback {
    public void sendCommand(
            final CommandCategory commandCategory,
            final long chatId,
            final List<Long> receiverPeerIdList,
            final String commandBody);
}
