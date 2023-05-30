package com.mcdead.busycoder.socialcipher.client.activity.error.fragment.model;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.fragment.ErrorFragmentCallback;

public class ErrorDialogFragmentViewModel extends ViewModel {
    private Error m_error = null;
    private ErrorFragmentCallback m_callback = null;

    public ErrorDialogFragmentViewModel() {
        super();
    }

    public boolean setError(final Error error) {
        if (error == null || m_error != null)
            return false;

        m_error = error;

        return true;
    }

    public boolean setCallback(final ErrorFragmentCallback callback) {
        if (callback == null || m_callback != null)
            return false;

        m_callback = callback;

        return true;
    }

    public Error getError() {
        return m_error;
    }

    public ErrorFragmentCallback getCallback() {
        return m_callback;
    }

    public boolean isInitialized() {
        return (m_error != null && m_callback != null);
    }
}
