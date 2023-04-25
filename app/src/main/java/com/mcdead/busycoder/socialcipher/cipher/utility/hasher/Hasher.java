package com.mcdead.busycoder.socialcipher.cipher.utility.hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    final private HashAlgorithm m_hashAlgorithm;

    public Hasher(
            final HashAlgorithm hashAlgorithm)
    {
        m_hashAlgorithm = hashAlgorithm;
    }

    public byte[] hashBytes(
            final byte[] sourceBytes)
    {
        if (sourceBytes == null) return null;
        if (sourceBytes.length != m_hashAlgorithm.m_dataSize)
            return null;

        byte[] outputBytes = null;

        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(m_hashAlgorithm.getName());

            outputBytes = messageDigest.digest(sourceBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;
        }

        if (outputBytes.length != m_hashAlgorithm.m_dataSize)
            return null;

        return outputBytes;
    }

    public enum HashAlgorithm {
        SHA_256("SHA256", 256),
        MD_5("MD5", 128);

        final private String m_name;
        final private int m_dataSize;

        private HashAlgorithm(
                final String name,
                final int dataSize)
        {
            m_name = name;
            m_dataSize = dataSize;
        }

        public String getName() {
            return m_name;
        }

        public static HashAlgorithm getAlgorithmByDataSize(
                final int dataSize)
        {
            if (dataSize <= 0) return null;

            for (final HashAlgorithm hashAlgorithm : HashAlgorithm.values())
                if (hashAlgorithm.m_dataSize == dataSize)
                    return hashAlgorithm;

            return null;
        }
    }
}
