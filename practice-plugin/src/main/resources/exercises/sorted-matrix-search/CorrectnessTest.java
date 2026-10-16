package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridContainsNothing() {
        check(new int[0][], 3, false);
    }

    @Test
    void rowsWithoutColumnsContainNothing() {
        check(new int[][]{{}, {}}, 3, false);
    }

    @Test
    void everyValueIsFound() {
        int[][] grid = {{-9, -5, 0}, {2, 8, 11}, {13, 14, 40}, {41, 50, 99}};
        for (int[] row : grid) {
            for (int value : row) {
                check(grid, value, true);
            }
        }
    }

    @Test
    void everyGapIsRejected() {
        int[][] grid = {{-9, -5, 0}, {2, 8, 11}, {13, 14, 40}, {41, 50, 99}};
        for (int target : new int[]{-10, -7, -1, 1, 5, 9, 12, 20, 45, 100}) {
            check(grid, target, false);
        }
    }

    @Test
    void singleRow() {
        check(new int[][]{{2, 4, 6, 8}}, 8, true);
        check(new int[][]{{2, 4, 6, 8}}, 5, false);
    }

    @Test
    void singleColumn() {
        check(new int[][]{{1}, {3}, {5}}, 3, true);
        check(new int[][]{{1}, {3}, {5}}, 4, false);
        check(new int[][]{{1}, {3}, {5}}, 5, true);
    }

    @Test
    void targetBeyondBothEnds() {
        check(new int[][]{{10, 20}, {30, 40}}, 5, false);
        check(new int[][]{{10, 20}, {30, 40}}, 50, false);
    }

    @Test
    void extremeValues() {
        check(new int[][]{{Integer.MIN_VALUE, 0}, {1, Integer.MAX_VALUE}}, Integer.MAX_VALUE, true);
    }

    private static void check(int[][] grid, int target, boolean expected) {
        String call = "containsValue(%s, %d)".formatted(Arrays.deepToString(grid), target);
        assertThat(Solution.containsValue(grid, target)).as(call).isEqualTo(expected);
    }
}
