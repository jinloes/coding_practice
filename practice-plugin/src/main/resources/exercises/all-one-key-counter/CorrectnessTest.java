package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void decrementingMissingKeyIsANoOp() {
        Solution counter = new Solution();
        counter.dec("ghost");
        assertThat(counter.getMaxKey()).isEmpty();
        assertThat(counter.getMinKey()).isEmpty();
    }

    @Test
    void movedKeysCanBecomeNewMinimum() {
        Solution counter = new Solution();
        counter.inc("a");
        counter.inc("a");
        counter.inc("b");
        counter.inc("b");
        counter.inc("c");
        counter.dec("a");
        assertThat(counter.getMinKey()).isIn("a", "c");
        assertThat(counter.getMaxKey()).isEqualTo("b");
    }

    @Test
    void maxAndMinUpdateAfterBucketRemoval() {
        Solution counter = new Solution();
        counter.inc("a");
        counter.inc("b");
        counter.inc("b");
        counter.dec("b");
        assertThat(counter.getMaxKey()).isIn("a", "b");
        assertThat(counter.getMinKey()).isIn("a", "b");
        counter.dec("a");
        assertThat(counter.getMaxKey()).isEqualTo("b");
        assertThat(counter.getMinKey()).isEqualTo("b");
    }

    @Test
    void matchesOracleDuringRandomUpdates() {
        Solution counter = new Solution();
        Map<String, Integer> oracle = new HashMap<>();
        Random random = new Random(9);
        String[] keys = {"a", "b", "c", "d", "e", "f"};
        for (int step = 0; step < 500; step++) {
            String key = keys[random.nextInt(keys.length)];
            if (random.nextInt(3) == 0) {
                counter.dec(key);
                oracle.computeIfPresent(key, (ignored, count) -> count == 1 ? null : count - 1);
            } else {
                counter.inc(key);
                oracle.merge(key, 1, Integer::sum);
            }
            assertExtrema(counter, oracle, step);
        }
    }

    private static void assertExtrema(Solution counter, Map<String, Integer> oracle, int step) {
        if (oracle.isEmpty()) {
            assertThat(counter.getMaxKey()).as("step %d max", step).isEmpty();
            assertThat(counter.getMinKey()).as("step %d min", step).isEmpty();
            return;
        }
        int max = oracle.values().stream().mapToInt(Integer::intValue).max().orElseThrow();
        int min = oracle.values().stream().mapToInt(Integer::intValue).min().orElseThrow();
        assertThat(oracle.get(counter.getMaxKey())).as("step %d max", step).isEqualTo(max);
        assertThat(oracle.get(counter.getMinKey())).as("step %d min", step).isEqualTo(min);
    }
}
