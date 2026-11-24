package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void evictsLeastFrequentlyUsedKey() {
        Solution cache = new Solution(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assertThat(cache.get(1)).isEqualTo(10);
        cache.put(3, 30);
        assertThat(cache.get(2)).isEqualTo(-1);
        assertThat(cache.get(3)).isEqualTo(30);
    }

    @Test
    void breaksFrequencyTiesByLeastRecentlyUsed() {
        Solution cache = new Solution(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        assertThat(cache.get(1)).isEqualTo(-1);
        assertThat(cache.get(2)).isEqualTo(2);
    }

    @Test
    void zeroCapacityCacheIgnoresInserts() {
        Solution cache = new Solution(0);
        cache.put(1, 1);
        assertThat(cache.get(1)).isEqualTo(-1);
    }
}
