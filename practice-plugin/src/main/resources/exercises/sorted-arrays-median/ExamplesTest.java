package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void oddCountTakesTheMiddleValue() {
        check(new int[]{1, 3}, new int[]{2}, 2.0);
    }

    @Test
    void evenCountAveragesTheMiddlePair() {
        check(new int[]{1, 2}, new int[]{3, 4}, 2.5);
    }

    @Test
    void oneEmptyArray() {
        check(new int[]{}, new int[]{5}, 5.0);
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
