package com.company.shortenurl.shortenurl.hash;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TU_MurmurHash3Implementation {
    final String CODE ="TESTHASHCODE";

    @InjectMocks
    MurmurHash3Implementation murmurHash3Implementation;

    @Test
    void hash32() {
        String hashcode = murmurHash3Implementation.hash32(CODE);
        assertFalse(hashcode.isEmpty());
    }

    @Test
    void hash64() {
        String hashcode = murmurHash3Implementation.hash64(CODE);
        assertFalse(hashcode.isEmpty());
    }

    @Test
    void testHash32() {
        String hashcode = murmurHash3Implementation.hash32(CODE.getBytes(),5);
        assertFalse(hashcode.isEmpty());
    }

    @Test
    void testHash64() {
        String hashcode = murmurHash3Implementation.hash32(CODE.getBytes(),5);
        assertFalse(hashcode.isEmpty());
    }
}