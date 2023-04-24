package com.mcdead.busycoder.socialcipher.client.activity.error.data;

import java.io.Serializable;

public class Error implements Serializable {
    private String m_message = null;
    private boolean m_isCritical = false;

    public Error(final String message,
                 final boolean isCritical)
    {
        m_message = message;
        m_isCritical = isCritical;
    }

    public String getMessage() {
        return m_message;
    }

    public boolean isCritical() {
        return m_isCritical;
    }
}
