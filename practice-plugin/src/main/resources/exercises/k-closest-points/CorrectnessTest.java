package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void pointsOnTheOriginComeFirst() {
        assertClosest(new int[][]{{5, 5}, {0, 0}, {1, 1}, {0, 0}}, 2, "k=2 with duplicate origin points");
    }

    @Test
    void euclideanDiffersFromManhattan() {
        assertClosest(new int[][]{{3, 3}, {0, 5}, {5, 0}, {4, 0}}, 2, "k=2 where |x|+|y| ranks differently");
    }

    @Test
    void tiesAtTheBoundaryMayGoEitherWay() {
        assertClosest(new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {9, 9}}, 2, "k=2 with four tied points");
    }

    @Test
    void negativeCoordinates() {
        assertClosest(new int[][]{{-8, -8}, {-1, -2}, {-3, 0}, {7, -7}}, 3, "k=3 with negative coordinates");
    }

    @Test
    void duplicatePointsAreSeparate() {
        assertClosest(new int[][]{{2, 1}, {2, 1}, {2, 1}, {9, 9}}, 2, "k=2 over repeated points");
    }

    @Test
    void zeroKReturnsNothing() {
        assertClosest(new int[][]{{1, 1}, {2, 2}}, 0, "k=0");
    }

    @Test
    void largeCoordinatesDoNotOverflow() {
        assertClosest(new int[][]{{40000, 40000}, {1, 1}, {-40000, 3}, {30000, 0}}, 2, "k=2 with squares above int range");
    }

    @Test
    void rejectsKOutsideTheInputSize() {
        assertThatThrownBy(() -> Solution.closest(new int[][]{{1, 1}}, 2)).as("k above size").isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Solution.closest(new int[][]{{1, 1}}, -1)).as("negative k").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void matchesSortingOnPseudoRandomPoints() {
        java.util.Random random = new java.util.Random(21);
        for (int round = 0; round < 30; round++) {
            int[][] points = new int[40][];
            for (int i = 0; i < points.length; i++) {
                points[i] = new int[]{random.nextInt(21) - 10, random.nextInt(21) - 10};
            }
            assertClosest(points, 1 + random.nextInt(40), "random round " + round);
        }
    }

    private static long squared(int[] point) {
        return (long) point[0] * point[0] + (long) point[1] * point[1];
    }

    /** Asserts the answer holds k of the input points whose distances are the k smallest. */
    private static void assertClosest(int[][] points, int k, String label) {
        int[][] copy = new int[points.length][];
        for (int i = 0; i < points.length; i++) {
            copy[i] = points[i].clone();
        }
        int[][] result = Solution.closest(copy, k);
        assertThat(result.length).as(label + ": result size").isEqualTo(k);
        List<Long> all = new ArrayList<>();
        for (int[] point : points) {
            all.add(squared(point));
        }
        Collections.sort(all);
        List<Long> got = new ArrayList<>();
        List<String> remaining = new ArrayList<>();
        for (int[] point : points) {
            remaining.add(point[0] + "," + point[1]);
        }
        for (int[] point : result) {
            got.add(squared(point));
            assertThat(remaining.remove(point[0] + "," + point[1])).as(label + ": " + Arrays.toString(point) + " must be an unused input point").isTrue();
        }
        Collections.sort(got);
        assertThat(got).as(label + ": distances of the chosen points").isEqualTo(all.subList(0, k));
        for (int i = 0; i < points.length; i++) {
            assertThat(copy[i]).as(label + ": input point " + i + " must not change").containsExactly(points[i]);
        }
    }
}
