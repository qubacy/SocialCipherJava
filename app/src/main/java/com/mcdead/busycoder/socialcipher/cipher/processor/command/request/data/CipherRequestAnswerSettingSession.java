package com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data;

import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswerType;

import java.io.Serializable;

public class CipherRequestAnswerSettingSession extends CipherRequestAnswer
        implements Serializable
{
    final private boolean m_answer;

    public CipherRequestAnswerSettingSession(
            final boolean answer)
    {
        m_answer = answer;
    }

    public boolean getAnswer() {
        return m_answer;
    }

    @Override
    public RequestAnswerType getRequestType() {
        return RequestAnswerType.SETTING_CIPHER_SESSION;
    }
}
