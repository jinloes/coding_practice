package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void crossesInTwoHops() {
        check(new int[]{3, 1, 1, 2, 1, 4}, 2);
    }

    @Test
    void unitStepsNeedEveryHop() {
        check(new int[]{1, 1, 1, 1}, 3);
    }

    @Test
    void singleStoneNeedsNoHops() {
        check(new int[]{5}, 0);
    }

    private static void check(int[] reach, int expected) {
        String call = "minJumps(%s)".formatted(Arrays.toString(reach));
        assertThat(Solution.minJumps(reach)).as(call).isEqualTo(expected);
    }
}
