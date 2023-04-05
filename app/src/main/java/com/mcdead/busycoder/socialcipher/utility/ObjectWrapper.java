package com.mcdead.busycoder.socialcipher.utility;

public class ObjectWrapper<T> {
    private T m_value = null;

    public ObjectWrapper() {

    }

    public ObjectWrapper(final T value) {
        m_value = value;
    }

    public boolean setValue(final T value) {
        if (value == null || m_value != null)
            return false;

        m_value = value;

        return true;
    }

    public T getValue() {
        return m_value;
    }
}
