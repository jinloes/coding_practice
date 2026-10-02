package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void needsTwoRoomsForPartialOverlap() {
        check(iv(1, 4, 2, 5, 4, 6), 2);
    }

    @Test
    void reusesRoomFreedAtStartTime() {
        check(iv(1, 2, 2, 3), 1);
    }

    @Test
    void needsThreeRoomsWhenAllOverlap() {
        check(iv(1, 5, 2, 6, 3, 7), 3);
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
