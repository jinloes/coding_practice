package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAValueInTheMiddle() {
        check(new int[][]{{1, 4, 7}, {9, 12, 15}, {20, 22, 30}}, 12, true);
    }

    @Test
    void rejectsAValueBetweenRows() {
        check(new int[][]{{1, 4, 7}, {9, 12, 15}, {20, 22, 30}}, 10, false);
    }

    @Test
    void singleCellGrid() {
        check(new int[][]{{5}}, 5, true);
    }

    private static void check(int[][] grid, int target, boolean expected) {
        String call = "containsValue(%s, %d)".formatted(Arrays.deepToString(grid), target);
        assertThat(Solution.containsValue(grid, target)).as(call).isEqualTo(expected);
    }
}
