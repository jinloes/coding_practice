package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noPointsCostNothing() {
        assertThat(Solution.minimumCost(new int[0][]))
                .as("minimumCost of 0 points")
                .isEqualTo(0);
    }

    @Test
    void duplicatePointsCostNothing() {
        assertThat(Solution.minimumCost(new int[][]{{2, 2}, {2, 2}, {2, 2}}))
                .as("minimumCost of 3 points")
                .isEqualTo(0);
    }

    @Test
    void collinearPointsInScrambledOrder() {
        assertThat(Solution.minimumCost(new int[][]{{9, 0}, {0, 0}, {6, 0}, {3, 0}, {12, 0}}))
                .as("minimumCost of 5 points")
                .isEqualTo(12);
    }

    @Test
    void negativeCoordinates() {
        assertThat(Solution.minimumCost(new int[][]{{-5, -5}, {-5, 3}, {4, -1}, {0, 0}, {-2, 7}}))
                .as("minimumCost of 5 points")
                .isEqualTo(28);
    }

    @Test
    void threeByThreeLatticeNeedsEightUnits() {
        assertThat(Solution.minimumCost(new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 2}, {2, 0}, {2, 1}, {2, 2}}))
                .as("minimumCost of 9 points")
                .isEqualTo(8);
    }

    @Test
    void chainFromFirstPointIsNotOptimal() {
        assertThat(Solution.minimumCost(new int[][]{{5, 0}, {0, 0}, {10, 0}, {5, 4}, {5, -4}}))
                .as("minimumCost of 5 points")
                .isEqualTo(18);
    }

    @Test
    void twoClustersJoinedOnce() {
        assertThat(Solution.minimumCost(new int[][]{{0, 0}, {0, 1}, {1, 0}, {100, 100}, {100, 101}, {101, 100}}))
                .as("minimumCost of 6 points")
                .isEqualTo(203);
    }

    @Test
    void largeCoordinates() {
        assertThat(Solution.minimumCost(new int[][]{{-1000000, -1000000}, {1000000, 1000000}, {0, 0}}))
                .as("minimumCost of 3 points")
                .isEqualTo(4000000);
    }

    @Test
    void manyScrambledCollinearPoints() {
        int count = 1_024;
        int[][] points = new int[count][];
        for (int i = 0; i < count; i++) {
            points[i] = new int[]{3 * ((i * 37) % count), 0};
        }
        assertThat(Solution.minimumCost(points)).as("minimumCost of %d collinear points", count).isEqualTo(3 * (count - 1));
    }

    @Test
    void doesNotChangeThePoints() {
        int[][] points = {{3, 3}, {0, 0}, {1, 8}};
        Solution.minimumCost(points);
        assertThat(points).isDeepEqualTo(new int[][]{{3, 3}, {0, 0}, {1, 8}});
    }
}
