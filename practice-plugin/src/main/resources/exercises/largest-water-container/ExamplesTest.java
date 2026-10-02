package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void picksTheBestPairOfWalls() {
        check(new int[]{1, 5, 2, 6, 3}, 10);
    }

    @Test
    void handlesTwoEqualWalls() {
        check(new int[]{4, 4}, 4);
    }

    @Test
    void returnsZeroForASingleWall() {
        check(new int[]{7}, 0);
    }

    private static void check(int[] heights, long expected) {
        String call = "maxArea(%s)".formatted(Arrays.toString(heights));
        assertThat(Solution.maxArea(heights)).as(call).isEqualTo(expected);
    }
}
