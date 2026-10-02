package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyStringHasNoDecoding() {
        assertCount("", 0);
    }

    @Test
    void singleNonZeroDigit() {
        assertCount("7", 1);
    }

    @Test
    void lonelyZero() {
        assertCount("0", 0);
    }

    @Test
    void tenIsOneWay() {
        assertCount("10", 1);
    }

    @Test
    void hundredIsInvalid() {
        assertCount("100", 0);
    }

    @Test
    void twentySixIsTwoWays() {
        assertCount("26", 2);
    }

    @Test
    void twentySevenIsOneWay() {
        assertCount("27", 1);
    }

    @Test
    void zeroInTheMiddleForcesAPair() {
        assertCount("2101", 1);
    }

    @Test
    void zeroAfterLargePairIsInvalid() {
        assertCount("230", 0);
    }

    @Test
    void allZeros() {
        assertCount("0000", 0);
    }

    @Test
    void repeatedOnes() {
        assertCount("1111", 5);
    }

    @Test
    void mixedZerosAndOnes() {
        assertCount("11106", 2);
    }

    @Test
    void pairsOfTenAndTwenty() {
        assertCount("1020", 1);
    }

    @Test
    void leadingZeroAfterValidPrefix() {
        assertCount("1006", 0);
    }

    @Test
    void longRunOfOnesIsReducedModuloTheBound() {
        assertCount("1".repeat(100), 782204094);
    }

    @Test
    void longRunOfOnesWithLargerCount() {
        assertCount("1".repeat(1000), 107579939);
    }

    @Test
    void longRunOfSingleOnlyDigitsHasOneWay() {
        assertCount("7".repeat(200000), 1);
    }

    private static void assertCount(String digits, int expected) {
        assertThat(Solution.count(digits)).as("count(\"%s\")", digits).isEqualTo(expected);
    }
}
