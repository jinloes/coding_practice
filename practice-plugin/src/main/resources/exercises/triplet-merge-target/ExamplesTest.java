package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void skipsTheOvershootingTriplet() {
        check(new int[][]{{2, 5, 3}, {1, 8, 4}, {1, 7, 5}}, new int[]{2, 7, 5}, true);
    }

    @Test
    void oneTripletAlreadyEqualsTheTarget() {
        check(new int[][]{{3, 4, 5}, {4, 5, 6}}, new int[]{3, 4, 5}, true);
    }

    @Test
    void noTripletReachesTheThirdValue() {
        check(new int[][]{{2, 3, 1}, {1, 2, 2}}, new int[]{2, 3, 3}, false);
    }

    private static void check(int[][] triplets, int[] target, boolean expected) {
        String call = "canMerge(%s, %s)".formatted(Arrays.deepToString(triplets), Arrays.toString(target));
        assertThat(Solution.canMerge(triplets, target)).as(call).isEqualTo(expected);
    }
}
