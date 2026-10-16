package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void startsAtTheLastStation() {
        check(new int[]{4, 2, 3}, new int[]{3, 4, 2}, true);
    }

    @Test
    void shortOnFuelHasNoStart() {
        check(new int[]{1, 2}, new int[]{3, 3}, false);
    }

    @Test
    void singleStationBreakingEven() {
        check(new int[]{5}, new int[]{5}, true);
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
