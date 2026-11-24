package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsIntervalOnInteriorRemoval() {
        Solution rm = new Solution();
        rm.addRange(10, 20);
        rm.removeRange(14, 16);
        assertThat(rm.queryRange(10, 14)).isTrue();
        assertThat(rm.queryRange(13, 15)).isFalse();
        assertThat(rm.queryRange(16, 17)).isTrue();
    }

    @Test
    void mergesAdjacentRanges() {
        Solution rm = new Solution();
        rm.addRange(1, 5);
        rm.addRange(5, 10);
        assertThat(rm.queryRange(2, 9)).isTrue();
    }

    @Test
    void removesAcrossMultipleIntervals() {
        Solution rm = new Solution();
        rm.addRange(1, 5);
        rm.addRange(7, 12);
        rm.removeRange(3, 9);
        assertThat(rm.queryRange(1, 3)).isTrue();
        assertThat(rm.queryRange(9, 12)).isTrue();
        assertThat(rm.queryRange(3, 4)).isFalse();
    }
}
