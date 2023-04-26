package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;

public class CipherCommandDataInitRequest extends CipherCommandData {
    final private CipherAlgorithm m_cipherAlgorithm;
    final private CipherMode m_cipherMode;
    final private CipherPadding m_cipherPadding;

    private CipherCommandDataInitRequest(
            final CipherAlgorithm cipherAlgorithm,
            final CipherMode cipherMode,
            final CipherPadding cipherPadding)
    {
        m_cipherAlgorithm = cipherAlgorithm;
        m_cipherMode = cipherMode;
        m_cipherPadding = cipherPadding;
    }

    public static CipherCommandDataInitRequest getInstance(
            final CipherAlgorithm cipherAlgorithm,
            final CipherMode cipherMode,
            final CipherPadding cipherPadding)
    {
        if (cipherAlgorithm == null
         || cipherMode == null
         || cipherPadding == null)
        {
            return null;
        }

        return new CipherCommandDataInitRequest(
                cipherAlgorithm,
                cipherMode,
                cipherPadding);
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
}
