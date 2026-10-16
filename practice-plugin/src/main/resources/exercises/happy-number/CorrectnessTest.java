package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void sevenIsHappy() {
        check(7, true);
    }

    @Test
    void tenIsHappy() {
        check(10, true);
    }

    @Test
    void twoIsUnhappy() {
        check(2, false);
    }

    @Test
    void cycleMemberIsUnhappy() {
        check(145, false);
    }

    @Test
    void largeHappy() {
        check(1_000_000_000, true);
    }

    @Test
    void maxIntTerminates() {
        check(Integer.MAX_VALUE, false);
    }

    @Test
    void firstHundredMatchKnownHappyList() {
        int[] happy = {1, 7, 10, 13, 19, 23, 28, 31, 32, 44, 49, 68, 70, 79, 82, 86, 91, 94, 97, 100};
        int found = 0;
        for (int n = 1; n <= 100; n++) {
            boolean expected = found < happy.length && happy[found] == n;
            if (expected) {
                found++;
            }
            check(n, expected);
        }
    }

    private static void check(int n, boolean expected) {
        assertThat(Solution.isHappy(n)).as("isHappy(%d)".formatted(n)).isEqualTo(expected);
    }
}
