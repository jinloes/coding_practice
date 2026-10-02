package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheTwoMostFrequentValues() {
        assertThat(Solution.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2))
                .as("topKFrequent([1, 1, 1, 2, 2, 3], 2)").containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void handlesASingleValue() {
        assertThat(Solution.topKFrequent(new int[]{5}, 1)).as("topKFrequent([5], 1)").containsExactly(5);
    }

    @Test
    void handlesNegativeValues() {
        assertThat(Solution.topKFrequent(new int[]{4, 4, -1, -1, -1, 7}, 1))
                .as("topKFrequent([4, 4, -1, -1, -1, 7], 1)").containsExactly(-1);
    }
}
