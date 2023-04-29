package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;

public class CipherCommandDataInitRequest extends CipherCommandData {
    final private CipherAlgorithm m_cipherAlgorithm;
    final private CipherMode m_cipherMode;
    final private CipherPadding m_cipherPadding;
    final private CipherKeySize m_cipherKeySize;
    final private long m_startTimeMilliseconds;

    private CipherCommandDataInitRequest(
            final CipherAlgorithm cipherAlgorithm,
            final CipherMode cipherMode,
            final CipherPadding cipherPadding,
            final CipherKeySize cipherKeySize,
            final long startTimeMilliseconds)
    {
        m_cipherAlgorithm = cipherAlgorithm;
        m_cipherMode = cipherMode;
        m_cipherPadding = cipherPadding;
        m_cipherKeySize = cipherKeySize;
        m_startTimeMilliseconds = startTimeMilliseconds;
    }

    public static CipherCommandDataInitRequest getInstance(
            final CipherAlgorithm cipherAlgorithm,
            final CipherMode cipherMode,
            final CipherPadding cipherPadding,
            final CipherKeySize cipherKeySize,
            final long startTimeMilliseconds)
    {
        if (cipherAlgorithm == null
         || cipherMode == null
         || cipherPadding == null
         || cipherKeySize == null
         || startTimeMilliseconds <= 0)
        {
            return null;
        }

        return new CipherCommandDataInitRequest(
                cipherAlgorithm,
                cipherMode,
                cipherPadding,
                cipherKeySize,
                startTimeMilliseconds);
    }

    public CipherAlgorithm getCipherAlgorithm() {
        return m_cipherAlgorithm;
    }

    public CipherMode getCipherMode() {
        return m_cipherMode;
    }

    public CipherPadding getCipherPadding() {
        return m_cipherPadding;
    }

    public CipherKeySize getCipherKeySize() {
        return m_cipherKeySize;
    }

    public long getStartTimeMilliseconds() {
        return m_startTimeMilliseconds;
    }

    @Override
    public CipherCommandType getType() {
        return CipherCommandType.CIPHER_SESSION_INIT_REQUEST;
    }
}
