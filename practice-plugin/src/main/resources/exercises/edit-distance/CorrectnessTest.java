package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void identicalWordsCostNothing() {
        check("same", "same", 0);
    }

    @Test
    void twoEmptyWordsCostNothing() {
        check("", "", 0);
    }

    @Test
    void clearingAWordCostsItsLength() {
        check("abcd", "", 4);
    }

    @Test
    void replacesOneLetter() {
        check("cat", "cut", 1);
    }

    @Test
    void reversedWordsNeedTwoReplacements() {
        check("abc", "cba", 2);
    }

    @Test
    void swappedPairNeedsTwoEdits() {
        check("ab", "ba", 2);
    }

    @Test
    void mixesAllThreeKinds() {
        check("intention", "execution", 5);
    }

    @Test
    void horseToRos() {
        check("horse", "ros", 3);
    }

    @Test
    void sundayToSaturday() {
        check("sunday", "saturday", 3);
    }

    @Test
    void gardenToDanger() {
        check("garden", "danger", 4);
    }

    @Test
    void longMismatchReplacesEveryLetter() {
        check("a".repeat(200), "b".repeat(200), 200);
    }

    @Test
    void longPrefixGrowth() {
        check("ab".repeat(150), "ab".repeat(150) + "z", 1);
    }

    private static void check(String from, String to, int expected) {
        String call = "minEdits(\"%s\", \"%s\")".formatted(from, to);
        assertThat(Solution.minEdits(from, to)).as(call).isEqualTo(expected);
    }
}
