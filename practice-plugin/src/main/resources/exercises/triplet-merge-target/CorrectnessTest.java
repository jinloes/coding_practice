package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyInputCannotMerge() {
        check(new int[0][], new int[]{1, 1, 1}, false);
    }

    @Test
    void tripletHigherInOnePositionIsUnusable() {
        check(new int[][]{{2, 9, 0}, {0, 2, 0}, {0, 0, 2}}, new int[]{2, 2, 2}, false);
    }

    @Test
    void threeSingleHitTripletsCombine() {
        check(new int[][]{{5, 0, 0}, {0, 5, 0}, {0, 0, 5}}, new int[]{5, 5, 5}, true);
    }

    @Test
    void exactMatchAmongOthers() {
        check(new int[][]{{9, 9, 9}, {1, 2, 3}}, new int[]{1, 2, 3}, true);
    }

    @Test
    void missingExactHitInOnePosition() {
        check(new int[][]{{5, 5, 4}, {4, 5, 5}}, new int[]{5, 5, 5}, true);
    }

    @Test
    void smallerValuesNeverReachTarget() {
        check(new int[][]{{1, 1, 1}, {2, 2, 2}}, new int[]{3, 3, 3}, false);
    }

    @Test
    void negativeValues() {
        check(new int[][]{{-1, -5, -3}, {-4, -2, -9}}, new int[]{-1, -2, -3}, true);
    }

    @Test
    void manyUnsafeTripletsAndOneSafe() {
        check(manyUnsafe(300), new int[]{4, 4, 4}, true);
    }

    private static int[][] manyUnsafe(int count) {
        int[][] triplets = new int[count + 1][];
        for (int i = 0; i < count; i++) {
            triplets[i] = new int[]{5 + i % 3, 4, 4};
        }
        triplets[count] = new int[]{4, 4, 4};
        return triplets;
    }

    private static void check(int[][] triplets, int[] target, boolean expected) {
        String call = "canMerge(%s, %s)".formatted(Arrays.deepToString(triplets), Arrays.toString(target));
        assertThat(Solution.canMerge(triplets, target)).as(call).isEqualTo(expected);
    }
}
