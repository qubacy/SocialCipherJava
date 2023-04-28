package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

public class CipherCommandDataSessionSet extends CipherCommandData {
    private CipherCommandDataSessionSet() {

    }

    public static CipherCommandDataSessionSet getInstance() {
        return new CipherCommandDataSessionSet();
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_SET;
    }
}
