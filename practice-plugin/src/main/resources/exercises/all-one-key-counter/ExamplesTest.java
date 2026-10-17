package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reportsDifferentMaximumAndMinimum() {
        Solution counter = new Solution();
        counter.inc("apple");
        counter.inc("banana");
        counter.inc("apple");
        assertThat(counter.getMaxKey()).isEqualTo("apple");
        assertThat(counter.getMinKey()).isEqualTo("banana");
    }

    @Test
    void removingLastKeyLeavesCounterEmpty() {
        Solution counter = new Solution();
        counter.inc("a");
        counter.dec("a");
        assertThat(counter.getMaxKey()).isEmpty();
        assertThat(counter.getMinKey()).isEmpty();
    }

    @Test
    void tiesMayReturnAnyTiedKey() {
        Solution counter = new Solution();
        counter.inc("x");
        counter.inc("y");
        assertThat(counter.getMaxKey()).isIn("x", "y");
        assertThat(counter.getMinKey()).isIn("x", "y");
    }
}
