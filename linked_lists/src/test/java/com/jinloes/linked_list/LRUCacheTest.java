package com.jinloes.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LRUCacheTest {
    private LRUCache lruCache;

    @BeforeEach
    void setUp() {
        this.lruCache = new LRUCache(2);
    }

    @Test
    void case1() {
        lruCache.put(1, 10);

        assertThat(lruCache.get(1))
                .isEqualTo(10);

        lruCache.put(2, 20);
        lruCache.put(3, 30);

        assertThat(lruCache.get(2))
                .isEqualTo(20);

        assertThat(lruCache.get(1))
                .isEqualTo(-1);
    }

    @Test
    void case2() {
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

    @Test
    public void case3() {
        lruCache = new LRUCache(3);

        lruCache.put(1, 1);
        lruCache.put(2, 2);
        lruCache.put(3, 3);

        assertThat(lruCache.get(1)).isEqualTo(1);
        assertThat(lruCache.get(2)).isEqualTo(2);
        assertThat(lruCache.get(4)).isEqualTo(-1);

        lruCache.put(4, 4);

        assertThat(lruCache.get(1)).isEqualTo(1);
        assertThat(lruCache.get(2)).isEqualTo(2);
        assertThat(lruCache.get(3)).isEqualTo(-1);
        assertThat(lruCache.get(4)).isEqualTo(4);
        assertThat(lruCache.get(2)).isEqualTo(2);

        lruCache.put(1, 8);
        lruCache.put(3, 7);

        assertThat(lruCache.get(1)).isEqualTo(8);
        assertThat(lruCache.get(2)).isEqualTo(2);
        assertThat(lruCache.get(3)).isEqualTo(7);
        assertThat(lruCache.get(4)).isEqualTo(-1);
        assertThat(lruCache.get(5)).isEqualTo(-1);
        assertThat(lruCache.get(2)).isEqualTo(2);
        assertThat(lruCache.get(3)).isEqualTo(7);
        assertThat(lruCache.get(4)).isEqualTo(-1);

        lruCache.put(1, 9);
        lruCache.put(6, 6);

        assertThat(lruCache.get(1)).isEqualTo(9);
        assertThat(lruCache.get(2)).isEqualTo(-1);
        assertThat(lruCache.get(3)).isEqualTo(7);
        assertThat(lruCache.get(4)).isEqualTo(-1);
        assertThat(lruCache.get(5)).isEqualTo(-1);
        assertThat(lruCache.get(6)).isEqualTo(6);
    }
}