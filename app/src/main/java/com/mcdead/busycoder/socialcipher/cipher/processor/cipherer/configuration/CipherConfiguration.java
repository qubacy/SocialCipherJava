package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CipherConfiguration {
    private static final HashMap<CipherAlgorithm, List<CipherKeySize>> C_VALID_MODE_KEY_SIZE_SET_HASH_MAP
            = new HashMap<CipherAlgorithm, List<CipherKeySize>>()
    {
        {
            put(CipherAlgorithm.AES, new ArrayList<CipherKeySize>(){
                {
                    add(CipherKeySize.KEY_192);
                    add(CipherKeySize.KEY_256);
                }
            });
        }
    };

    final private CipherAlgorithm m_cipherAlgorithm;
    final private CipherMode m_cipherMode;
    final private CipherPadding m_cipherPadding;
    final private CipherKeySize m_keySize;

    private CipherConfiguration(
            final CipherAlgorithm algorithm,
            final CipherMode mode,
            final CipherPadding padding,
            final CipherKeySize keySize)
    {
        m_cipherAlgorithm = algorithm;
        m_cipherMode = mode;
        m_cipherPadding = padding;
        m_keySize = keySize;
    }

    public static CipherConfiguration getInstance(
            final CipherAlgorithm algorithm,
            final CipherMode mode,
            final CipherPadding padding,
            final CipherKeySize keySize)
    {
        if (!checkConfigurationValidity(algorithm, mode, padding, keySize))
            return null;

        return new CipherConfiguration(algorithm, mode, padding, keySize);
    }

    private static boolean checkConfigurationValidity(
            final CipherAlgorithm algorithm,
            final CipherMode mode,
            final CipherPadding padding,
            final CipherKeySize keySize)
    {
        if (algorithm == null || mode == null || padding == null || keySize == null)
            return false;

        List<CipherKeySize> cipherKeySizeList =
                C_VALID_MODE_KEY_SIZE_SET_HASH_MAP.get(algorithm);

        if (cipherKeySizeList == null)
            return false;

        return cipherKeySizeList.contains(keySize);
    }

    public CipherAlgorithm getAlgorithm() {
        return m_cipherAlgorithm;
    }

    public CipherMode getMode() {
        return m_cipherMode;
    }

    public CipherPadding getPadding() {
        return m_cipherPadding;
    }

    public CipherKeySize getKeySize() {
        return m_keySize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CipherConfiguration that = (CipherConfiguration) o;

        return m_cipherAlgorithm == that.m_cipherAlgorithm &&
                m_cipherMode == that.m_cipherMode &&
                m_cipherPadding == that.m_cipherPadding &&
                m_keySize == that.m_keySize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_cipherAlgorithm, m_cipherMode, m_cipherPadding, m_keySize);
    }
}
