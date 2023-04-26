package com.mcdead.busycoder.socialcipher.command;

import java.util.List;

public interface CommandProcessorCallback {
    public void sendCommand(
            final long chatId,
            final List<Long> receiverPeerIdList,
            final String commandBody);
}
