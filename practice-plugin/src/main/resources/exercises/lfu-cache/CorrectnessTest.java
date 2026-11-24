package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void evictsLowerFrequencyKeyEvenWhenMoreRecentlyUsed() {
        Solution cache = new Solution(2);
        cache.put(1, 10);
        assertThat(cache.get(1)).isEqualTo(10); // freq(1)=2
        cache.put(2, 20);                       // freq(2)=1, more recent than 1
        cache.put(3, 30);                       // must evict 2 (lower freq), not 1 (older)
        assertThat(cache.get(2)).isEqualTo(-1);
        assertThat(cache.get(1)).isEqualTo(10);
        assertThat(cache.get(3)).isEqualTo(30);
    }

    @Test
    void updatingExistingKeyIncrementsFrequencyAndRefreshesRecency() {
        Solution cache = new Solution(2);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(1, 100);
        cache.put(3, 30);
        assertThat(cache.get(2)).isEqualTo(-1);
        assertThat(cache.get(1)).isEqualTo(100);
        assertThat(cache.get(3)).isEqualTo(30);
    }

    @Test
    void evictsLruAmongTiedHigherFrequencyTier() {
        Solution cache = new Solution(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assertThat(cache.get(1)).isEqualTo(10);
        assertThat(cache.get(2)).isEqualTo(20);
        // Both have frequency 2; key 1 is older than key 2.
        cache.put(3, 30);
        assertThat(cache.get(1)).isEqualTo(-1);
        assertThat(cache.get(2)).isEqualTo(20);
        assertThat(cache.get(3)).isEqualTo(30);
    }

    @Test
    void capacityOneAlwaysEvictsPriorKeyOnNewInsert() {
        Solution cache = new Solution(1);
        cache.put(1, 1);
        assertThat(cache.get(1)).isEqualTo(1);
        assertThat(cache.get(1)).isEqualTo(1);
        cache.put(2, 2);
        assertThat(cache.get(1)).isEqualTo(-1);
        assertThat(cache.get(2)).isEqualTo(2);
    }

    @Test
    void minFreqAdvancesWhenLowestTierDrains() {
        Solution cache = new Solution(3);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        assertThat(cache.get(1)).isEqualTo(1);
        assertThat(cache.get(2)).isEqualTo(2);
        assertThat(cache.get(3)).isEqualTo(3);
        assertThat(cache.get(1)).isEqualTo(1);
        // Frequencies: 1->3, 2->2 (oldest in tier 2), 3->2.
        cache.put(4, 4);
        assertThat(cache.get(2)).isEqualTo(-1);
        assertThat(cache.get(1)).isEqualTo(1);
        assertThat(cache.get(3)).isEqualTo(3);
        assertThat(cache.get(4)).isEqualTo(4);
    }
}
