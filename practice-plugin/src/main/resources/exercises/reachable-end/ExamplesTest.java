package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reachesEndExactly() {
        assertResult(new int[]{2, 3, 1, 1, 4}, true);
    }

    @Test
    void blockedByZero() {
        assertResult(new int[]{3, 2, 1, 0, 4}, false);
    }

    @Test
    void singleCell() {
        assertResult(new int[]{0}, true);
    }

    private static void assertResult(int[] values, boolean expected) {
        int[] copy = values.clone();
        boolean actual = Solution.canReach(values);
        assertThat(actual).as("canReach(%s)", Arrays.toString(copy)).isEqualTo(expected);
        assertThat(values).as("canReach must not mutate its input").containsExactly(copy);
    }
}
