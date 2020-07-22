package com.company.shortenurl.shortenurl.hash;

/**
 * Interface for Generating the Hash function.
 */
public interface HashFunction {

    public String hash32(final String text);

    public String hash64(final String text);

    public String hash32(final byte[] data, int length);

    public String hash64(final byte[] data, int length);
}
