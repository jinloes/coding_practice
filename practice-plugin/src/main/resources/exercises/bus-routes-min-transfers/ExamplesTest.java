package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void reachesTargetWithOneTransfer() {
        assertThat(solution.minBuses(new int[][]{{1, 2, 7}, {3, 6, 7}}, 1, 6)).isEqualTo(2);
    }

    @Test
    void returnsMinusOneWhenDisconnected() {
        assertThat(solution.minBuses(
                new int[][]{{7, 12}, {4, 5, 15}, {6}, {15, 19}, {9, 12, 13}}, 15, 12))
                .isEqualTo(-1);
    }

    @Test
    void returnsZeroWhenSourceEqualsTarget() {
        assertThat(solution.minBuses(new int[][]{{1, 2, 3}}, 5, 5)).isEqualTo(0);
    }
}
