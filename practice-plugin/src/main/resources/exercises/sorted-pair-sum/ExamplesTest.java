package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsPairInTheMiddle() {
        check(new int[]{2, 5, 9, 11, 15}, 13, true);
    }

    @Test
    void allowsEqualValuesAtDifferentPositions() {
        check(new int[]{4, 4, 9}, 8, true);
    }

    @Test
    void returnsEmptyWhenNoPairExists() {
        check(new int[]{1, 3, 6}, 100, false);
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
