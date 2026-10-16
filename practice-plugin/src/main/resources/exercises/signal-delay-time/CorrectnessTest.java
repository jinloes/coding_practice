package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void onlyIncomingLinksToTheSourceLeavesOthersUnreached() {
        assertThat(Solution.signalDelay(new int[][]{{2, 1, 3}, {3, 1, 4}}, 3, 1))
                .as("signalDelay with 3 nodes from node 1")
                .isEqualTo(-1);
    }

    @Test
    void cheaperMultiHopRouteBeatsDirectLink() {
        assertThat(Solution.signalDelay(new int[][]{{1, 4, 10}, {1, 2, 1}, {2, 3, 1}, {3, 4, 2}}, 4, 1))
                .as("signalDelay with 4 nodes from node 1")
                .isEqualTo(4);
    }

    @Test
    void parallelLinksUseTheFastest() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 9}, {1, 2, 3}, {1, 2, 6}}, 2, 1))
                .as("signalDelay with 2 nodes from node 1")
                .isEqualTo(3);
    }

    @Test
    void zeroDelayLinksAreFree() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 0}, {2, 3, 0}, {3, 4, 7}}, 4, 1))
                .as("signalDelay with 4 nodes from node 1")
                .isEqualTo(7);
    }

    @Test
    void cyclesDoNotLoopForever() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 2}, {2, 3, 2}, {3, 1, 2}, {3, 4, 1}}, 4, 1))
                .as("signalDelay with 4 nodes from node 1")
                .isEqualTo(5);
    }

    @Test
    void sourceNeedNotBeNodeOne() {
        assertThat(Solution.signalDelay(new int[][]{{3, 1, 5}, {3, 2, 1}, {2, 1, 1}}, 3, 3))
                .as("signalDelay with 3 nodes from node 3")
                .isEqualTo(2);
    }

    @Test
    void answerIsTheSlowestNodeNotTheSum() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 5}, {1, 3, 2}, {1, 4, 9}}, 4, 1))
                .as("signalDelay with 4 nodes from node 1")
                .isEqualTo(9);
    }

    @Test
    void oneUnreachableNodeAmongManyGivesMinusOne() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 1}, {2, 3, 1}, {3, 4, 1}}, 5, 1))
                .as("signalDelay with 5 nodes from node 1")
                .isEqualTo(-1);
    }

    @Test
    void detourThroughAHubIsFaster() {
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 50}, {1, 4, 1}, {4, 2, 1}, {2, 3, 1}, {4, 3, 20}}, 4, 1))
                .as("signalDelay with 4 nodes from node 1")
                .isEqualTo(3);
    }

    @Test
    void longChainWithShortcuts() {
        int count = 5_000;
        int[][] links = new int[2 * (count - 1)][];
        int next = 0;
        for (int i = 1; i < count; i++) {
            links[next++] = new int[]{i, i + 1, 1};
            links[next++] = new int[]{i, Math.min(count, i + 2), 3};
        }
        assertThat(Solution.signalDelay(links, count, 1)).as("signalDelay over %d nodes", count).isEqualTo(count - 1);
    }

    @Test
    void doesNotChangeTheLinks() {
        int[][] links = {{1, 2, 3}, {2, 3, 4}};
        Solution.signalDelay(links, 3, 1);
        assertThat(links).isDeepEqualTo(new int[][]{{1, 2, 3}, {2, 3, 4}});
    }
}
