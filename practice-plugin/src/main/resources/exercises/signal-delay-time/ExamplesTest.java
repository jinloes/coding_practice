package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void slowestNodeSetsTheDelay() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 4}, {1, 3, 1}, {3, 2, 2}, {2, 4, 5}}, 4, 1))
                .as("signalDelay with 4 nodes from node 1")
                .isEqualTo(8);
    }

    @Test
    void unreachableNodeGivesMinusOne() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 3}}, 3, 1))
                .as("signalDelay with 3 nodes from node 1")
                .isEqualTo(-1);
    }

    @Test
    void lonelyNodeIsReachedInstantly() {
        assertThat(Solution.signalDelay(new int[0][], 1, 1))
                .as("signalDelay with 1 nodes from node 1")
                .isEqualTo(0);
    }
}
