package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

import java.util.Objects;

public class CipherCommandDataInitRequest extends CipherCommandData {
    final private CipherConfiguration m_cipherConfiguration;

    final private long m_startTimeMilliseconds;

    private CipherCommandDataInitRequest(
            final CipherConfiguration cipherConfiguration,
            final long startTimeMilliseconds)
    {
        m_cipherConfiguration = cipherConfiguration;
        m_startTimeMilliseconds = startTimeMilliseconds;
    }

    public static CipherCommandDataInitRequest getInstance(
            final CipherConfiguration cipherConfiguration,
            final long startTimeMilliseconds)
    {
        if (cipherConfiguration == null || startTimeMilliseconds <= 0)
            return null;

        return new CipherCommandDataInitRequest(
                cipherConfiguration,
                startTimeMilliseconds);
    }

    public CipherAlgorithm getCipherAlgorithm() {
        return m_cipherConfiguration.getAlgorithm();
    }

    public CipherMode getCipherMode() {
        return m_cipherConfiguration.getMode();
    }

    public CipherPadding getCipherPadding() {
        return m_cipherConfiguration.getPadding();
    }

    public CipherKeySize getCipherKeySize() {
        return m_cipherConfiguration.getKeySize();
    }

    public CipherConfiguration getCipherConfiguration() {
        return m_cipherConfiguration;
    }

    public long getStartTimeMilliseconds() {
        return m_startTimeMilliseconds;
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_REQUEST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CipherCommandDataInitRequest that = (CipherCommandDataInitRequest) o;

        return m_startTimeMilliseconds == that.m_startTimeMilliseconds &&
                Objects.equals(m_cipherConfiguration, that.m_cipherConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_cipherConfiguration, m_startTimeMilliseconds);
    }
}
