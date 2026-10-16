package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void oneHopCoversEverything() {
        check(new int[]{9, 1, 1}, 1);
    }

    @Test
    void classicTwoHopRow() {
        check(new int[]{2, 3, 1, 1, 4}, 2);
    }

    @Test
    void tieBetweenLandingSpotsPicksFartherReach() {
        check(new int[]{4, 4, 4, 2, 1, 4, 1, 4, 4}, 3);
    }

    @Test
    void zerosAreFineWhenJumpedOver() {
        check(new int[]{3, 0, 0, 5, 0}, 2);
    }

    @Test
    void lastStoneMayHoldZero() {
        check(new int[]{0}, 0);
    }

    @Test
    void growingReach() {
        check(new int[]{1, 2, 3}, 2);
    }

    @Test
    void longUnitRow() {
        check(ones(500), 499);
    }

    @Test
    void longRowOfTwos() {
        check(twos(501), 250);
    }

    private static int[] ones(int length) {
        int[] reach = new int[length];
        Arrays.fill(reach, 1);
        return reach;
    }

    private static int[] twos(int length) {
        int[] reach = new int[length];
        Arrays.fill(reach, 2);
        return reach;
    }

    private static void check(int[] reach, int expected) {
        String call = "minJumps(%s)".formatted(Arrays.toString(reach));
        assertThat(Solution.minJumps(reach)).as(call).isEqualTo(expected);
    }
}
