package com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader;

import android.os.AsyncTask;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.error.Error;

public abstract class DialogsLoaderBase extends AsyncTask<Void, Void, Error> {
    protected String m_token = null;
    protected DialogTypeDefinerVK m_dialogTypeDefiner = null;
    protected DialogsLoadingCallback m_callback = null;

    public DialogsLoaderBase(
            final String token,
            final DialogTypeDefinerVK dialogTypeDefiner,
            final DialogsLoadingCallback callback)
    {
        m_token = token;
        m_dialogTypeDefiner = dialogTypeDefiner;
        m_callback = callback;
    }
}
