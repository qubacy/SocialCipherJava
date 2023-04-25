package com.mcdead.busycoder.socialcipher.cipher.utility.hasher;

public class HasherGenerator {
    public static Hasher generateHasherWithDataSize(
            final int dataSize)
    {
        Hasher.HashAlgorithm hashAlgorithm = Hasher.HashAlgorithm.getAlgorithmByDataSize(dataSize);

        if (hashAlgorithm == null) return null;

        return generateHasher(hashAlgorithm);
    }

    public static Hasher generateHasher(
            final Hasher.HashAlgorithm hashAlgorithm)
    {
        if (hashAlgorithm == null) return null;

        return new Hasher(hashAlgorithm);
    }
}
