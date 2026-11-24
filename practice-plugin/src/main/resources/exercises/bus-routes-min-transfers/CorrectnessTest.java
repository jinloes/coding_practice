package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    private final Solution solution = new Solution();

    @Test
    void singleBusWhenSourceAndTargetShareRoute() {
        assertThat(solution.minBuses(new int[][]{{10, 20, 30, 40}}, 10, 40)).isEqualTo(1);
        assertThat(solution.minBuses(new int[][]{{10, 20, 30, 40, 50}}, 10, 30)).isEqualTo(1);
    }

    @Test
    void choosesShorterTransferChainOverLongerPath() {
        int[][] routes = {
                {1, 2},
                {2, 3},
                {3, 4},
                {1, 5, 4}
        };
        assertThat(solution.minBuses(routes, 1, 4)).isEqualTo(1);
    }

    @Test
    void handlesMultipleTransfersInChain() {
        int[][] routes = {
                {1, 2},
                {2, 3},
                {3, 4},
                {4, 5}
        };
        assertThat(solution.minBuses(routes, 1, 5)).isEqualTo(4);
    }

    @Test
    void handlesEmptyRoutes() {
        assertThat(solution.minBuses(new int[][]{}, 1, 2)).isEqualTo(-1);
        assertThat(solution.minBuses(new int[][]{}, 1, 1)).isEqualTo(0);
    }

    @Test
    void handlesRoutesWithDuplicateStopEntriesAndCycles() {
        int[][] routes = {
                {1, 2, 1, 3},
                {3, 4, 2},
                {4, 5, 6}
        };
        assertThat(solution.minBuses(routes, 1, 6)).isEqualTo(3);
    }
}
