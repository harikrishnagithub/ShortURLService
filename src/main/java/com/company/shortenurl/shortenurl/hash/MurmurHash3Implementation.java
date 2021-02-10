package com.company.shortenurl.shortenurl.hash;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * This class is for implementation murmur3 hashing technique.
 */
public class MurmurHash3Implementation implements com.company.shortenurl.shortenurl.hash.HashFunction {
    private static final int SEED = 999999999;

    /**
     *
     * @param text
     * @return
     */
    @Override
    public String hash32(String text) {
        if (text == null) {
            return null;
        }
        HashFunction mHash = Hashing.murmur3_32();
        HashCode hash = mHash.hashBytes(text.getBytes());
        return hash.toString();
    }

    /**
     * Hash code generation for 64 bit.
     * @param text
     * @return
     */
    @Override
    public String hash64(String text) {
        if (text == null) {
            return null;
        }
        HashFunction mHash = Hashing.murmur3_128();
        HashCode hash = mHash.hashBytes(text.getBytes());
        return hash.toString();
    }

    /**
     * hash code generation with length.
     * @param data
     * @param length
     * @return
     */
    @Override
    public String hash32(byte[] data, int length) {
        if (data.length == 0) {
            return null;
        }
        HashFunction mHash = Hashing.murmur3_32(SEED);
        HashCode hash = mHash.hashBytes(data, 0, length);
        return hash.toString();
    }

    /**
     * 64 bit hashcode generaion with fixed length.
     * @param data
     * @param length
     * @return
     */
    @Override
    public String hash64(byte[] data, int length) {
        if (data.length == 0) {
            return null;
        }
        HashFunction mHash = Hashing.murmur3_128(SEED);
        HashCode hash = mHash.hashBytes(data, 0, length);
        return hash.toString();
    }
}
