package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyModuleReturnsFalseForQueries() {
        Solution rm = new Solution();
        assertThat(rm.queryRange(0, 1)).isFalse();
        rm.removeRange(0, 10);
        assertThat(rm.queryRange(0, 1)).isFalse();
    }

    @Test
    void bridgesMultipleDisjointIntervals() {
        Solution rm = new Solution();
        rm.addRange(1, 3);
        rm.addRange(5, 7);
        rm.addRange(9, 11);
        rm.addRange(2, 10);
        assertThat(rm.queryRange(1, 11)).isTrue();
        assertThat(rm.queryRange(0, 11)).isFalse();
        assertThat(rm.queryRange(1, 12)).isFalse();
    }

    @Test
    void removesExactIntervalAndLeavesNeighborsIntact() {
        Solution rm = new Solution();
        rm.addRange(1, 5);
        rm.addRange(10, 15);
        rm.removeRange(1, 5);
        assertThat(rm.queryRange(1, 2)).isFalse();
        assertThat(rm.queryRange(10, 15)).isTrue();
    }

    @Test
    void repeatedAddAndRemoveOnSameCoordinates() {
        Solution rm = new Solution();
        rm.addRange(10, 30);
        rm.removeRange(15, 25);
        assertThat(rm.queryRange(10, 15)).isTrue();
        assertThat(rm.queryRange(15, 16)).isFalse();
        assertThat(rm.queryRange(25, 30)).isTrue();
        rm.addRange(15, 25);
        assertThat(rm.queryRange(10, 30)).isTrue();
    }
}
