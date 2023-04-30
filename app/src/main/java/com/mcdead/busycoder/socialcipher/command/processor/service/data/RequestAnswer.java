package com.mcdead.busycoder.socialcipher.command.processor.service.data;

import java.io.Serializable;

public class RequestAnswer implements Serializable {
    final private long m_messageId;

    public RequestAnswer(final long messageId) {
        m_messageId = messageId;
    }

    public long getMessageId() {
        return m_messageId;
    }
}
