package com.mcdead.busycoder.socialcipher.client.processor.network.update.processor;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class UpdateProcessorAsyncBase implements Runnable {
    protected String m_token = null;
    protected Context m_context = null;
    protected LinkedBlockingQueue<ResponseUpdateItemInterface> m_updateQueue = null;

    public UpdateProcessorAsyncBase(final String token,
                                    Context context,
                                    LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue)
    {
        m_token = token;
        m_context = context;
        m_updateQueue = updateQueue;
    }
}
