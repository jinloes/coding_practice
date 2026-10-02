package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void buildsTableForOne() {
        check(1, 0, 1);
    }

    @Test
    void buildsTableForSeven() {
        check(7, 0, 1, 1, 2, 1, 2, 2, 3);
    }

    @Test
    void handlesPowerOfTwoBoundary() {
        check(8, 0, 1, 1, 2, 1, 2, 2, 3, 1);
        checkAgainstBuiltIn(16);
    }

    @Test
    void handlesJustBelowPowerOfTwo() {
        checkAgainstBuiltIn(15);
        checkAgainstBuiltIn(255);
    }

    @Test
    void returnsOneEntryPerNumber() {
        assertThat(Solution.countBits(100)).hasSize(101);
    }

    @Test
    void matchesBuiltInForMediumTable() {
        checkAgainstBuiltIn(1000);
    }

    @Test
    void matchesBuiltInForLargeTable() {
        checkAgainstBuiltIn(100_000);
    }

    @Test
    void returnsIndependentArraysPerCall() {
        int[] first = Solution.countBits(4);
        first[1] = 99;
        assertThat(Solution.countBits(4)).containsExactly(0, 1, 1, 2, 1);
    }

    private static void check(int n, int... expected) {
        assertThat(Solution.countBits(n)).as("countBits(%d)", n).containsExactly(expected);
    }

    private static void checkAgainstBuiltIn(int n) {
        int[] table = Solution.countBits(n);
        assertThat(table).as("countBits(%d) length", n).hasSize(n + 1);
        for (int i = 0; i <= n; i++) {
            assertThat(table[i]).as("countBits(%d)[%d]", n, i).isEqualTo(Integer.bitCount(i));
        }
    }
}
