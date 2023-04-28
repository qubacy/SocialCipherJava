package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

public class CipherCommandDataInitAccept extends CipherCommandData {
    private CipherCommandDataInitAccept() {

    }

    public static CipherCommandDataInitAccept getInstance() {
        return new CipherCommandDataInitAccept();
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_ACCEPT;
    }
}
