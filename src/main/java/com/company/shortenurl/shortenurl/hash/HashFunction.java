package com.company.shortenurl.shortenurl.hash;

/**
 * Interface for Generating the Hash function.
 */
public interface HashFunction {

     String hash32(String text);

     String hash64(String text);

     String hash32(byte[] data, int length);

     String hash64(byte[] data, int length);
}
