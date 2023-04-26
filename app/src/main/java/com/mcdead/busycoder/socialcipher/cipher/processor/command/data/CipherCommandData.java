package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

public abstract class CipherCommandData {

    public CipherCommandData() {

    }

    public abstract CipherCommandType getType();
}
