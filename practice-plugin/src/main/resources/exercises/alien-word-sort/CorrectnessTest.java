package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndSingleWordListsAreSorted() {
        assertThat(Solution.isSorted(new String[]{}, "abc")).isTrue();
        assertThat(Solution.isSorted(new String[]{"solo"}, "abcdefghijklmnopqrstuvwxyz")).isTrue();
    }

    @Test
    void customOrderCanReverseNormalAlphabet() {
        assertThat(Solution.isSorted(new String[]{"b", "a"}, "bacdefghijklmnopqrstuvwxyz")).isTrue();
        assertThat(Solution.isSorted(new String[]{"a", "b"}, "bacdefghijklmnopqrstuvwxyz")).isFalse();
    }

    @Test
    void equalWordsDoNotBreakOrdering() {
        assertThat(Solution.isSorted(new String[]{"aa", "aa", "ab"}, "abcdefghijklmnopqrstuvwxyz")).isTrue();
    }

    @Test
    void laterAdjacentViolationIsDetected() {
        assertThat(Solution.isSorted(new String[]{"ant", "bat", "bar"}, "abcdefghijklmnopqrstuvwxyz")).isFalse();
    }

    @Test
    void checksManyAdjacentPairs() {
        String[] words = new String[500];
        for (int i = 0; i < words.length; i++) {
            words[i] = "a" + String.format("%03d", i);
        }
        assertThat(Solution.isSorted(words, "abcdefghijklmnopqrstuvwxyz0123456789")).isTrue();
        words[400] = "a000";
        assertThat(Solution.isSorted(words, "abcdefghijklmnopqrstuvwxyz0123456789")).isFalse();
    }
}
