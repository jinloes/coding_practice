package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void bothEmptyIsRejected() {
        assertThatThrownBy(() -> Solution.median(new int[]{}, new int[]{})).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void allEqualValues() {
        check(new int[]{0, 0}, new int[]{0, 0}, 0.0);
    }

    @Test
    void disjointRanges() {
        check(new int[]{1, 2, 3}, new int[]{10, 11}, 3.0);
    }

    @Test
    void interleavedEvenCount() {
        check(new int[]{1, 3, 5, 7}, new int[]{2, 4, 6, 8}, 4.5);
    }

    @Test
    void emptyArrayWithEvenCount() {
        check(new int[]{2, 4, 6, 8}, new int[]{}, 5.0);
    }

    @Test
    void extremeValuesDoNotOverflow() {
        check(new int[]{Integer.MAX_VALUE}, new int[]{Integer.MAX_VALUE}, 2147483647.0);
    }

    @Test
    void oppositeExtremesAverageToAHalf() {
        check(new int[]{Integer.MIN_VALUE}, new int[]{Integer.MAX_VALUE}, -0.5);
    }

    @Test
    void negativeValues() {
        check(new int[]{-5, -3}, new int[]{-4}, -4.0);
    }

    @Test
    void duplicatesAcrossArrays() {
        check(new int[]{1, 2}, new int[]{2, 3}, 2.0);
    }

    @Test
    void shortArrayInsideLongOne() {
        check(new int[]{50}, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, 5.5);
    }

    @Test
    void everyPrefixSplit() {
        int[] all = {-7, -2, 0, 3, 3, 8, 12, 15, 20, 41};
        for (int cut = 0; cut <= all.length; cut++) {
            int[] left = Arrays.copyOfRange(all, 0, cut);
            int[] right = Arrays.copyOfRange(all, cut, all.length);
            check(left, right, 5.5);
        }
    }

    private static void check(int[] first, int[] second, double expected) {
        String call = "median(%s, %s)".formatted(Arrays.toString(first), Arrays.toString(second));
        int[] firstCopy = first.clone();
        int[] secondCopy = second.clone();
        assertThat(Solution.median(first, second)).as(call).isEqualTo(expected);
        assertThat(Solution.median(second, first)).as(call + " with the arrays swapped").isEqualTo(expected);
        assertThat(first).as(call + " must not change first").containsExactly(firstCopy);
        assertThat(second).as(call + " must not change second").containsExactly(secondCopy);
    }
}
