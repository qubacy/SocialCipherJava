package com.mcdead.busycoder.socialcipher.cipher.data.entity.key;

import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.utility.hasher.Hasher;
import com.mcdead.busycoder.socialcipher.cipher.utility.hasher.HasherGenerator;

import java.nio.ByteBuffer;

public class CipherKeyGenerator {
    public static CipherKey generateCipherKeyWithSeed(
            final CipherKeySize size,
            final long seed)
    {
        if (size == null) return null;

        Hasher hasher =
                HasherGenerator.generateHasherWithDataSize(size.getIntSize());

        if (hasher == null) return null;

        byte[] seedBytes =
                ByteBuffer.allocate(Long.BYTES).putLong(seed).array();
        byte[] keyBytes = hasher.hashOneBlockBytes(seedBytes);

        if (keyBytes == null) return null;

        return new CipherKey(size, keyBytes);
    }

    public static CipherKey generateCipherKeyWithBytes(
            final CipherKeySize size,
            final byte[] bytes)
    {
        if (size == null || bytes == null)
            return null;
        if (bytes.length != size.getIntSize())
            return null;

        return new CipherKey(size, bytes);
    }

    public static CipherKey generateCipherKeyWithConfiguration(
            final CipherConfiguration cipherConfiguration,
            final byte[] bytes)
    {
        if (cipherConfiguration == null || bytes == null)
            return null;

        byte[] preparedBytes = null;

        if ((bytes.length * 8) != cipherConfiguration.getKeySize().getIntSize()) {
            Hasher hasher =
                    HasherGenerator.generateHasherWithDataSize(
                            cipherConfiguration.getKeySize().getIntSize());

            if (hasher == null) return null;

            preparedBytes = hasher.hashOneBlockBytes(bytes);

            if (preparedBytes == null) return null;

        }
        else
            preparedBytes = bytes;

        return new CipherKey(cipherConfiguration.getKeySize(), preparedBytes);
    }
}
