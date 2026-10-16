package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void picksTheNearestTwo() {
        assertClosest(new int[][]{{3, 3}, {1, -2}, {-1, 0}, {5, 1}}, 2, "k=2 over [3,3] [1,-2] [-1,0] [5,1]");
    }

    @Test
    void singlePointIsItsOwnAnswer() {
        assertClosest(new int[][]{{4, -7}}, 1, "k=1 over [4,-7]");
    }

    @Test
    void takesEverythingWhenKEqualsSize() {
        assertClosest(new int[][]{{2, 2}, {-2, 1}, {0, 5}}, 3, "k=3 over three points");
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
