package com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data;

import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswerType;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;

import java.io.Serializable;

public abstract class CipherRequestAnswer
        implements RequestAnswer, Serializable
{

    public abstract RequestAnswerType getRequestType();
}
