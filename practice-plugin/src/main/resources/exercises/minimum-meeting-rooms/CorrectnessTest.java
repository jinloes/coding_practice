package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void needsNoRoomsForEmptyCalendar() {
        check(iv(), 0);
    }

    @Test
    void needsOneRoomForSingleMeeting() {
        check(iv(3, 9), 1);
    }

    @Test
    void needsOneRoomPerIdenticalMeeting() {
        check(iv(2, 6, 2, 6, 2, 6, 2, 6), 4);
    }

    @Test
    void countsNestedMeetings() {
        check(iv(1, 10, 2, 9, 3, 8), 3);
    }

    @Test
    void needsOneRoomForBackToBackChain() {
        check(iv(4, 5, 1, 2, 3, 4, 2, 3), 1);
    }

    @Test
    void handlesUnsortedInput() {
        check(iv(15, 20, 5, 10, 0, 30, 10, 12), 2);
    }

    @Test
    void findsPeakAwayFromTheStart() {
        check(iv(0, 1, 5, 9, 6, 10, 7, 11, 20, 21), 3);
    }

    @Test
    void separatesEndsFromStartsCorrectly() {
        check(iv(1, 10, 2, 3, 4, 5, 6, 7), 2);
    }

    @Test
    void handlesNegativeValues() {
        check(iv(-10, -5, -7, -2, -5, 0), 2);
    }

    @Test
    void comparesExtremeValuesWithoutOverflow() {
        check(iv(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE), 2);
        check(iv(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE + 1), 1);
    }

    @Test
    void doesNotMutateTheInput() {
        int[][] meetings = iv(5, 8, 1, 3, 2, 6);
        Solution.minRooms(meetings);
        assertThat(meetings).as("input must stay unchanged and unsorted").isDeepEqualTo(iv(5, 8, 1, 3, 2, 6));
    }

    private static void check(int[][] meetings, int expected) {
        assertThat(Solution.minRooms(meetings))
                .as("minRooms(%s)", Arrays.deepToString(meetings))
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
