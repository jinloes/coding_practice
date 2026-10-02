package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noNodesMeansNoComponents() {
        assertThat(Solution.countComponents(0, new int[0][2])).isZero();
    }

    @Test
    void singleNodeIsOneComponent() {
        assertThat(Solution.countComponents(1, new int[0][2])).isEqualTo(1);
    }

    @Test
    void repeatedEdgesDoNotMergeAnythingExtra() {
        assertThat(Solution.countComponents(3, new int[][]{{0, 1}, {1, 0}, {0, 1}})).isEqualTo(2);
    }

    @Test
    void selfLoopsDoNotConnectAnything() {
        assertThat(Solution.countComponents(3, new int[][]{{0, 0}, {2, 2}})).isEqualTo(3);
    }

    @Test
    void laterEdgeMergesTwoLargeGroups() {
        assertThat(Solution.countComponents(6, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}})).isEqualTo(2);
        assertThat(Solution.countComponents(6, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 0}})).isEqualTo(1);
    }

    @Test
    void edgeOrderAndDirectionDoNotMatter() {
        assertThat(Solution.countComponents(5, new int[][]{{4, 3}, {2, 1}, {3, 2}})).isEqualTo(2);
    }

    @Test
    void starIsOneComponentWithIsolatedNodesCounted() {
        assertThat(Solution.countComponents(7, new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}})).isEqualTo(3);
    }

    @Test
    void longPathIsOneComponent() {
        int count = 50_000;
        int[][] edges = new int[count - 1][];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new int[]{i + 1, i};
        }
        assertThat(Solution.countComponents(count, edges)).isEqualTo(1);
    }

    @Test
    void pairsOfNodesFormManyComponents() {
        int count = 40_000;
        int[][] edges = new int[count / 2][];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new int[]{2 * i, 2 * i + 1};
        }
        assertThat(Solution.countComponents(count, edges)).isEqualTo(count / 2);
    }

    @Test
    void doesNotModifyTheEdges() {
        int[][] edges = {{0, 1}, {2, 1}};
        Solution.countComponents(3, edges);
        assertThat(edges).isDeepEqualTo(new int[][]{{0, 1}, {2, 1}});
    }
}
