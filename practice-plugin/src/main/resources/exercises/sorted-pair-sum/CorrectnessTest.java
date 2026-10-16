package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingleValueArrays() {
        check(new int[0], 0, false);
        check(new int[]{5}, 10, false);
    }

    @Test
    void findsPairAtTheTwoEnds() {
        check(new int[]{-7, 0, 3, 9, 20}, 13, true);
    }

    @Test
    void findsAdjacentPair() {
        check(new int[]{1, 2, 10, 11, 30}, 21, true);
    }

    @Test
    void doesNotUseOneIndexTwice() {
        check(new int[]{3, 7, 12}, 6, false);
        check(new int[]{3, 3}, 6, true);
    }

    @Test
    void handlesNegativeValuesAndTargets() {
        check(new int[]{-9, -5, -4, -1}, -10, true);
        check(new int[]{-9, -5, -4, -1}, -20, false);
    }

    @Test
    void doesNotOverflowWhenAddingLargeValues() {
        check(new int[]{Integer.MIN_VALUE, -5, 3, Integer.MAX_VALUE}, Integer.MAX_VALUE - 4, false);
        check(new int[]{Integer.MIN_VALUE, -5, 3, Integer.MAX_VALUE}, -2, true);
        check(new int[]{1_500_000_000, 2_000_000_000}, -794_967_296, false);
    }

    @Test
    void handlesManyDuplicates() {
        check(new int[]{2, 2, 2, 2, 2, 2}, 4, true);
        check(new int[]{2, 2, 2, 2, 2, 2}, 6, false);
    }

    @Test
    void scansALargeArray() {
        int[] values = new int[20_000];
        for (int i = 0; i < values.length; i++) {
            values[i] = 3 * i;
        }
        check(values, 3 * 9_999 + 3 * 10_001, true);
        check(values, 1, false);
    }

    private static void check(int[] sorted, int target, boolean exists) {
        int[] snapshot = sorted.clone();
        String call = "findPair(%s, %d)".formatted(Arrays.toString(sorted), target);
        int[] pair = Solution.findPair(sorted, target);
        assertThat(pair).as(call).isNotNull();
        assertThat(sorted).as(call + " must not mutate its input").containsExactly(snapshot);
        if (!exists) {
            assertThat(pair).as(call + " has no valid pair").isEmpty();
            return;
        }
        assertThat(pair).as(call + " must return two indices").hasSize(2);
        assertThat(pair[0]).as(call + " first index").isGreaterThanOrEqualTo(0).isLessThan(pair[1]);
        assertThat(pair[1]).as(call + " second index").isLessThan(sorted.length);
        assertThat((long) sorted[pair[0]] + sorted[pair[1]]).as(call + " pair sum").isEqualTo(target);
    }
}
