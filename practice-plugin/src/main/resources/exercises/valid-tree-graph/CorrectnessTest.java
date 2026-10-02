package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void twoNodesNeedExactlyOneEdge() {
        assertThat(Solution.isTree(2, new int[0][2])).isFalse();
        assertThat(Solution.isTree(2, new int[][]{{1, 0}})).isTrue();
    }

    @Test
    void tooFewEdgesLeaveNodesDisconnected() {
        assertThat(Solution.isTree(4, new int[][]{{0, 1}, {2, 3}})).isFalse();
    }

    @Test
    void rightEdgeCountWithCycleAndIsolatedNodeIsNotATree() {
        assertThat(Solution.isTree(4, new int[][]{{0, 1}, {1, 2}, {2, 0}})).isFalse();
        assertThat(Solution.isTree(5, new int[][]{{0, 1}, {1, 2}, {2, 0}, {3, 4}})).isFalse();
    }

    @Test
    void repeatedEdgeIsACycle() {
        assertThat(Solution.isTree(2, new int[][]{{0, 1}, {1, 0}})).isFalse();
        assertThat(Solution.isTree(3, new int[][]{{0, 1}, {0, 1}})).isFalse();
    }

    @Test
    void selfLoopIsACycle() {
        assertThat(Solution.isTree(1, new int[][]{{0, 0}})).isFalse();
        assertThat(Solution.isTree(2, new int[][]{{0, 0}})).isFalse();
    }

    @Test
    void starAndUnorderedEdgesAreTrees() {
        assertThat(Solution.isTree(5, new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}})).isTrue();
        assertThat(Solution.isTree(5, new int[][]{{3, 4}, {1, 0}, {2, 3}, {4, 0}})).isTrue();
    }

    @Test
    void longPathIsATree() {
        int count = 50_000;
        int[][] edges = new int[count - 1][];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new int[]{i, i + 1};
        }
        assertThat(Solution.isTree(count, edges)).isTrue();
    }

    @Test
    void longPathWithOneEdgeRedirectedIsNotATree() {
        int count = 50_000;
        int[][] edges = new int[count - 1][];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new int[]{i, i + 1};
        }
        edges[edges.length - 1] = new int[]{0, 2};
        assertThat(Solution.isTree(count, edges)).isFalse();
    }

    @Test
    void doesNotModifyTheEdges() {
        int[][] edges = {{0, 1}, {1, 2}};
        Solution.isTree(3, edges);
        assertThat(edges).isDeepEqualTo(new int[][]{{0, 1}, {1, 2}});
    }
}
