package com.mcdead.busycoder.socialcipher.cipher.data.entity.key;

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
        byte[] keyBytes = hasher.hashBytes(seedBytes);

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
}
