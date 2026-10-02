package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsZeroForEmptyInput() {
        check(new int[0], 0);
    }

    @Test
    void returnsZeroWhenAllWallsAreFlat() {
        check(new int[]{0, 0, 0}, 0);
    }

    @Test
    void handlesDescendingHeights() {
        check(new int[]{5, 4, 3, 2, 1}, 6);
    }

    @Test
    void handlesAscendingHeights() {
        check(new int[]{1, 2, 3, 4, 5}, 6);
    }

    @Test
    void prefersTallNeighboursOverWideShortWalls() {
        check(new int[]{2, 1, 100, 100, 1, 2}, 100);
    }

    @Test
    void prefersWidthWhenOuterWallsAreTall() {
        check(new int[]{5, 1, 1, 1, 1, 1, 6}, 30);
    }

    @Test
    void usesLongArithmeticForLargeAreas() {
        check(new int[]{Integer.MAX_VALUE, 0, Integer.MAX_VALUE}, 4294967294L);
    }

    @Test
    void handlesAZeroWallBesideTallOnes() {
        check(new int[]{0, 9, 9, 0}, 9);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] heights = {3, 1, 4, 1, 5};
        int[] before = heights.clone();
        Solution.maxArea(heights);
        assertThat(heights).as("maxArea must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] heights, long expected) {
        String call = "maxArea(%s)".formatted(Arrays.toString(heights));
        assertThat(Solution.maxArea(heights)).as(call).isEqualTo(expected);
    }
}
