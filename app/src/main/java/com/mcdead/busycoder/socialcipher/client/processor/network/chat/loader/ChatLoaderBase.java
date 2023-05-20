package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorBase;

public abstract class ChatLoaderBase extends AsyncTask<Void, Void, Error> {
    final protected String m_token;
    final protected ChatLoadingCallback m_callback;
    final protected long m_chatId;
    final protected MessageProcessorBase m_messageProcessor;

    protected ChatLoaderBase(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId,
            final MessageProcessorBase messageProcessor)
    {
        m_token = token;
        m_callback = callback;
        m_chatId = chatId;
        m_messageProcessor = messageProcessor;
    }
}
