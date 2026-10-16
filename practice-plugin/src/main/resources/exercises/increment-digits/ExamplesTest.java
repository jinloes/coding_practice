package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void carriesOnePosition() {
        check(new int[]{1, 2, 9}, 1, 3, 0);
    }

    @Test
    void growsWhenAllNines() {
        check(new int[]{9, 9}, 1, 0, 0);
    }

    @Test
    void zeroBecomesOne() {
        check(new int[]{0}, 1);
    }

    private static void check(int[] digits, int... expected) {
        int[] original = digits.clone();
        String call = "increment(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.increment(digits)).as(call).containsExactly(expected);
        assertThat(digits).as(call + " must not mutate the input").containsExactly(original);
    }
}
