package com.mcdead.busycoder.socialcipher.command.processor;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;

public interface CommandProcessor {
    public Error processCommand(final CommandData commandData);
    public void execState();
}
