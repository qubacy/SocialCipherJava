package com.mcdead.busycoder.socialcipher.client.processor.network.update.processor;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class UpdateProcessorAsyncBase implements Runnable {
    final protected String m_token;
    final protected Context m_context;
    final protected LinkedBlockingQueue<ResponseUpdateItemInterface> m_updateQueue;

    protected UpdateProcessorAsyncBase(
            final String token,
            Context context,
            LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue)
    {
        m_token = token;
        m_context = context;
        m_updateQueue = updateQueue;
    }
}
