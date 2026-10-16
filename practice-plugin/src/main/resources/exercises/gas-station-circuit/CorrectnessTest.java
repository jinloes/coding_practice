package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleStationShortOfFuel() {
        check(new int[]{2}, new int[]{3}, false);
    }

    @Test
    void everyStartWorksWhenBalanced() {
        check(new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}, true);
    }

    @Test
    void richStationInTheMiddle() {
        check(new int[]{1, 1, 9, 1}, new int[]{3, 3, 2, 3}, true);
    }

    @Test
    void totalExactlyEnoughButNarrow() {
        check(new int[]{2, 0, 0, 6}, new int[]{3, 2, 1, 2}, true);
    }

    @Test
    void totalOneShort() {
        check(new int[]{2, 0, 0, 5}, new int[]{3, 2, 1, 2}, false);
    }

    @Test
    void surplusAtTheEnd() {
        check(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2}, true);
    }

    @Test
    void largeValuesDoNotOverflow() {
        check(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 0}, new int[]{0, 0, Integer.MAX_VALUE}, true);
    }

    @Test
    void longRowWithLateStart() {
        check(lateStartGain(400), lateStartCost(400), true);
    }

    private static int[] lateStartGain(int length) {
        int[] gain = new int[length];
        gain[length - 1] = length;
        return gain;
    }

    private static int[] lateStartCost(int length) {
        int[] cost = new int[length];
        Arrays.fill(cost, 1);
        return cost;
    }

    private static void check(int[] gain, int[] cost, boolean possible) {
        String call = "startingStation(%s, %s)".formatted(Arrays.toString(gain), Arrays.toString(cost));
        int start = Solution.startingStation(gain, cost);
        if (!possible) {
            assertThat(start).as(call).isEqualTo(-1);
            return;
        }
        assertThat(start).as(call).isBetween(0, gain.length - 1);
        assertThat(completes(gain, cost, start)).as(call + " returned " + start + ", which must finish the circuit").isTrue();
    }

    private static boolean completes(int[] gain, int[] cost, int start) {
        long tank = 0;
        for (int step = 0; step < gain.length; step++) {
            int station = (start + step) % gain.length;
            tank += gain[station] - cost[station];
            if (tank < 0) {
                return false;
            }
        }
        return true;
    }
}
