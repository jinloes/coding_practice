package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void allowsBackToBackMeetings() {
        check(iv(9, 10, 10, 11, 13, 14), true);
    }

    @Test
    void detectsOverlap() {
        check(iv(1, 5, 4, 8), false);
    }

    @Test
    void allowsEmptyCalendar() {
        check(iv(), true);
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
