package com.mcdead.busycoder.socialcipher.utility;

import java.util.Objects;

public class ObjectWrapper<T>{
    private T m_value = null;

    public ObjectWrapper() {

    }

    public ObjectWrapper(final T value) {
        m_value = value;
    }

    public boolean setValue(final T value) {
        if (value == null)
            return false;

        m_value = value;

        return true;
    }

    public T getValue() {
        return m_value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ObjectWrapper<?> that = (ObjectWrapper<?>) o;

        return Objects.equals(m_value, that.m_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_value);
    }
}
