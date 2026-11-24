package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingleCharacter() {
        assertThat(Solution.canBePalindrome("")).as("empty string").isTrue();
        assertThat(Solution.canBePalindrome("z")).as("single character").isTrue();
        assertThat(Solution.canBePalindrome("ab")).as("two distinct characters").isTrue();
    }

    @Test
    void mustCheckBothBranchesWhenBothImmediateNeighborsMatch() {
        // Skipping left gives "cuucu" (not a palindrome); skipping right gives "ucuuc" -> wait:
        // "aguokepatgbnvfqmgmlcupuufxoohdfpgjdmysgvhmvffcnqxjjxqncffvmhvgsymdjgpfdhooxfuupuculmgmqfvnbgtapekouga"
        String leftSkipFailsRightSkipSucceeds = "ebcbbececabbacecbbcbe";
        assertThat(Solution.canBePalindrome(leftSkipFailsRightSkipSucceeds))
                .as("must try skipping right when skipping left fails later")
                .isTrue();
        String rightSkipFailsLeftSkipSucceeds = new StringBuilder(leftSkipFailsRightSkipSucceeds).reverse().toString();
        assertThat(Solution.canBePalindrome(rightSkipFailsLeftSkipSucceeds))
                .as("must try skipping left when skipping right fails later")
                .isTrue();
    }

    @Test
    void handlesDeletionAtOuterBoundary() {
        assertThat(Solution.canBePalindrome("xracecar")).as("delete first char").isTrue();
        assertThat(Solution.canBePalindrome("racecarx")).as("delete last char").isTrue();
    }

    @Test
    void rejectsMultipleMismatches() {
        assertThat(Solution.canBePalindrome("abccba".replace('c', 'x') + "yz")).as("two extra chars").isFalse();
        assertThat(Solution.canBePalindrome("abcdef")).as("all distinct").isFalse();
        assertThat(Solution.canBePalindrome("tebbem")).as("needs two deletions").isFalse();
    }

    @Test
    void handlesLongPalindromeWithOneInsertedCharacter() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5_000; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        String half = sb.toString();
        String text = half + "z" + sb.reverse();
        assertThat(Solution.canBePalindrome(text)).as("long palindrome with center char").isTrue();
        String broken = "xy" + half + new StringBuilder(half).reverse();
        assertThat(Solution.canBePalindrome(broken)).as("two mismatches in long string").isFalse();
    }
}
