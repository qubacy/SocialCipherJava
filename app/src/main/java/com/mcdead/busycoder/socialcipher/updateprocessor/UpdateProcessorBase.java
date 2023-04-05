package com.mcdead.busycoder.socialcipher.updateprocessor;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class UpdateProcessorBase implements Runnable {
    protected String m_token = null;
    protected Context m_context = null;
    protected LinkedBlockingQueue<ResponseUpdateItemInterface> m_updateQueue = null;

    public UpdateProcessorBase(final String token,
                               Context context,
                               LinkedBlockingQueue<ResponseUpdateItemInterface> updateQueue)
    {
        m_token = token;
        m_context = context;
        m_updateQueue = updateQueue;
    }
}
