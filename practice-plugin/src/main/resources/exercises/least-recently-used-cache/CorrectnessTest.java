package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void rejectsCapacityBelowOne() {
        assertThatThrownBy(() -> new Solution(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(-3)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void missingKeyReturnsMinusOne() {
        Solution cache = new Solution(3);
        assertThat(cache.get(42)).isEqualTo(-1);
        cache.put(1, 1);
        assertThat(cache.get(2)).isEqualTo(-1);
    }

    @Test
    void valueZeroIsStoredLikeAnyOther() {
        Solution cache = new Solution(2);
        cache.put(8, 0);
        assertThat(cache.get(8)).isZero();
    }

    @Test
    void evictsInLeastRecentlyUsedOrder() {
        Solution cache = new Solution(3);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.get(1);
        cache.get(2);
        cache.put(4, 4);
        assertThat(cache.get(3)).as("3 was the least recently used").isEqualTo(-1);
        cache.put(5, 5);
        assertThat(cache.get(1)).as("1 became the least recently used").isEqualTo(-1);
        assertThat(cache.get(2)).isEqualTo(2);
        assertThat(cache.get(4)).isEqualTo(4);
        assertThat(cache.get(5)).isEqualTo(5);
    }

    @Test
    void overwriteDoesNotEvictWhenFull() {
        Solution cache = new Solution(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(2, 22);
        assertThat(cache.get(1)).as("an overwrite must not evict").isEqualTo(1);
        assertThat(cache.get(2)).isEqualTo(22);
    }

    @Test
    void missedReadsDoNotDisturbOrder() {
        Solution cache = new Solution(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.get(99);
        cache.put(3, 3);
        assertThat(cache.get(1)).as("key 1 was least recently used").isEqualTo(-1);
        assertThat(cache.get(2)).isEqualTo(2);
    }

    @Test
    void matchesAnOracleOnRandomOperations() {
        Random random = new Random(21);
        for (int capacity = 1; capacity <= 5; capacity++) {
            Solution cache = new Solution(capacity);
            Oracle oracle = new Oracle(capacity);
            for (int step = 0; step < 400; step++) {
                int key = random.nextInt(8);
                if (random.nextBoolean()) {
                    int value = random.nextInt(1000);
                    cache.put(key, value);
                    oracle.put(key, value);
                } else {
                    assertThat(cache.get(key)).as("capacity %d, step %d: get(%d)", capacity, step, key)
                            .isEqualTo(oracle.get(key));
                }
            }
        }
    }

    private static final class Oracle {
        private final int capacity;
        private final LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();

        Oracle(int capacity) {
            this.capacity = capacity;
        }

        int get(int key) {
            Integer value = map.remove(key);
            if (value == null) {
                return -1;
            }
            map.put(key, value);
            return value;
        }

        void put(int key, int value) {
            map.remove(key);
            if (map.size() == capacity) {
                map.remove(map.keySet().iterator().next());
            }
            map.put(key, value);
        }
    }
}
