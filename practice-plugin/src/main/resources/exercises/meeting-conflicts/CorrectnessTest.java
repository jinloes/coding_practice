package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void allowsSingleMeeting() {
        check(iv(3, 4), true);
    }

    @Test
    void detectsOverlapInUnsortedInput() {
        check(iv(20, 25, 1, 5, 4, 8, 10, 12), false);
    }

    @Test
    void allowsUnsortedNonOverlappingMeetings() {
        check(iv(20, 25, 1, 5, 5, 10, 10, 12), true);
    }

    @Test
    void detectsIdenticalMeetings() {
        check(iv(2, 3, 2, 3), false);
    }

    @Test
    void detectsNestedMeeting() {
        check(iv(1, 10, 3, 4), false);
    }

    @Test
    void detectsConflictBetweenFirstTwoSortedMeetings() {
        check(iv(1, 3, 2, 4, 5, 6, 7, 8), false);
    }

    @Test
    void detectsConflictBetweenLastTwoSortedMeetings() {
        check(iv(1, 2, 3, 4, 5, 8, 7, 9), false);
    }

    @Test
    void handlesNegativeValues() {
        check(iv(-5, -1, -1, 0, 0, 3), true);
        check(iv(-5, -1, -2, 0), false);
    }

    @Test
    void comparesExtremeValuesWithoutOverflow() {
        check(iv(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE + 1), true);
        check(iv(0, Integer.MAX_VALUE, Integer.MIN_VALUE, 1), false);
    }

    @Test
    void doesNotMutateTheInput() {
        int[][] meetings = iv(5, 8, 1, 3);
        Solution.canAttendAll(meetings);
        assertThat(meetings).as("input must stay unchanged and unsorted").isDeepEqualTo(iv(5, 8, 1, 3));
    }

    private static void check(int[][] meetings, boolean expected) {
        assertThat(Solution.canAttendAll(meetings))
                .as("canAttendAll(%s)", Arrays.deepToString(meetings))
                .isEqualTo(expected);
    }

    private static int[][] iv(int... flat) {
        int[][] result = new int[flat.length / 2][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new int[]{flat[2 * i], flat[2 * i + 1]};
        }
        return result;
    }
}
