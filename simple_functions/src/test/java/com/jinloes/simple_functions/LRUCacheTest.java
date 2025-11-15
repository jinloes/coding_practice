package com.jinloes.simple_functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {
    private LRUCache lruCache;

    @BeforeEach
    void setUp() {
        this.lruCache = new LRUCache(2);
    }

    @Test
    void get() {
        lruCache.put(1, 1);
        lruCache.put(2, 2);

        assertThat(lruCache.get(1))
                .isEqualTo(1);

        lruCache.put(3, 3);

        assertThat(lruCache.get(2))
                .isEqualTo(-1);

        lruCache.put(4, 4);

        assertThat(lruCache.get(1))
                .isEqualTo(-1);

        assertThat(lruCache.get(3))
                .isEqualTo(3);

        assertThat(lruCache.get(4))
                .isEqualTo(4);
    }
}