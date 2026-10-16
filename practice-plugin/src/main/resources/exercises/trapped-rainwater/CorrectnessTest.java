package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesShortInputs() {
        check(new int[0], 0);
        check(new int[]{7}, 0);
        check(new int[]{4, 9}, 0);
    }

    @Test
    void flatAndDescendingRowsHoldNothing() {
        check(new int[]{5, 5, 5, 5}, 0);
        check(new int[]{9, 7, 4, 1}, 0);
        check(new int[]{0, 0, 0}, 0);
    }

    @Test
    void waterLevelIsLimitedByTheLowerWall() {
        check(new int[]{5, 0, 0, 2}, 4);
        check(new int[]{2, 0, 0, 5}, 4);
    }

    @Test
    void handlesSeveralBasins() {
        check(new int[]{4, 1, 3, 0, 2, 5, 1, 4}, 13);
    }

    @Test
    void innerWallsSplitTheWater() {
        check(new int[]{3, 0, 3, 0, 3}, 6);
        check(new int[]{3, 0, 1, 0, 3}, 8);
    }

    @Test
    void sumsAboveTheIntRange() {
        check(new int[]{Integer.MAX_VALUE, 0, 0, 0, Integer.MAX_VALUE}, 3L * Integer.MAX_VALUE);
    }

    @Test
    void handlesWideBasinWithBumps() {
        check(new int[]{6, 1, 2, 1, 4, 1, 1, 5}, 20);
    }

    @Test
    void scansALargeRow() {
        int[] heights = new int[40_000];
        heights[0] = 1_000;
        heights[heights.length - 1] = 1_000;
        check(heights, 1_000L * (heights.length - 2));
    }

    private static void check(int[] heights, long expected) {
        int[] snapshot = heights.clone();
        String call = "trap(%s)".formatted(Arrays.toString(heights));
        assertThat(Solution.trap(heights)).as(call).isEqualTo(expected);
        assertThat(heights).as(call + " must not mutate its input").containsExactly(snapshot);
    }
}
