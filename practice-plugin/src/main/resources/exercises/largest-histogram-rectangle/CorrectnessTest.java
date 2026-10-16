package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void zeroHeightBarsGiveNoArea() {
        check(new int[]{0, 0}, 0);
    }

    @Test
    void risingBarsPickTheMiddleCut() {
        check(new int[]{1, 2, 3, 4, 5}, 9);
    }

    @Test
    void fallingBarsPickTheMiddleCut() {
        check(new int[]{5, 4, 3, 2, 1}, 9);
    }

    @Test
    void zeroBarSplitsTheHistogram() {
        check(new int[]{1, 1, 1, 0, 5, 5}, 10);
    }

    @Test
    void lowBarBetweenTallOnes() {
        check(new int[]{2, 0, 2}, 2);
    }

    @Test
    void mixedHeights() {
        check(new int[]{9, 3, 7, 7, 7, 2, 8}, 21);
    }

    @Test
    void singleBar() {
        check(new int[]{6}, 6);
    }

    @Test
    void areaBeyondIntRange() {
        check(new int[]{2_000_000_000, 2_000_000_000, 2_000_000_000}, 6_000_000_000L);
    }

    @Test
    void lowBarInTheMiddleSplitsTallOnes() {
        check(new int[]{4, 9, 1, 9, 4}, 9);
    }

    private static void check(int[] heights, long expected) {
        int[] original = heights.clone();
        String call = "largestRectangle(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.largestRectangle(heights)).as(call).isEqualTo(expected);
        assertThat(heights).as(call + " must not change the input").containsExactly(original);
    }
}
