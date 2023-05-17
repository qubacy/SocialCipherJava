package com.mcdead.busycoder.socialcipher.client.activity.error.data;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Error error = (Error) o;

        return m_isCritical == error.m_isCritical &&
                Objects.equals(m_message, error.m_message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_message, m_isCritical);
    }
}
