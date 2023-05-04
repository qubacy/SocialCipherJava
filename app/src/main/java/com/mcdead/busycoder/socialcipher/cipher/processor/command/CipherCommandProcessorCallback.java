package com.mcdead.busycoder.socialcipher.cipher.processor.command;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.command.processor.CommandProcessorCallback;

public interface CipherCommandProcessorCallback extends CommandProcessorCallback {
    public CipherRequestAnswerSettingSession onCipherSessionSettingRequestReceived();
    public void onCipherSessionSettingEnded(
            final boolean isCipherSessionSet,
            final long chatId);
    public long getLocalPeerId();
}
