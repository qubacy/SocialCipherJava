package com.mcdead.busycoder.socialcipher.utility;

public class ObjectWrapperSynchronized<T> {
    private volatile T m_value = null;

    public ObjectWrapperSynchronized() {

    }

    public ObjectWrapperSynchronized(
            final T value)
    {
        m_value = value;
    }

    public synchronized boolean setValue(
            final T value)
    {
        m_value = value;

        return true;
    }

    public synchronized T getValue() {
        return m_value;
    }
}
