package com.mcdead.busycoder.socialcipher.cipher.processor.command;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;

public interface CipherCommandProcessorCallback {
    public CipherRequestAnswerSettingSession onCipherSessionSettingRequestReceived();
}
