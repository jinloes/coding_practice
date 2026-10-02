package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesASingleCharacter() {
        assertThat(Solution.longestUniqueLength("q")).isEqualTo(1);
    }

    @Test
    void doesNotMoveTheWindowBackward() {
        assertThat(Solution.longestUniqueLength("abba")).isEqualTo(2);
    }

    @Test
    void restartsAfterTheMostRecentRepeat() {
        assertThat(Solution.longestUniqueLength("dvdf")).isEqualTo(3);
    }

    @Test
    void findsALaterBestWindow() {
        assertThat(Solution.longestUniqueLength("tmmzuxt")).isEqualTo(5);
    }

    @Test
    void acceptsAllDistinctCharacters() {
        assertThat(Solution.longestUniqueLength("abcdef")).isEqualTo(6);
    }

    @Test
    void countsSpacesAndPunctuation() {
        assertThat(Solution.longestUniqueLength("a b a!")).isEqualTo(4);
    }

    @Test
    void distinguishesUppercaseFromLowercase() {
        assertThat(Solution.longestUniqueLength("aAaA")).isEqualTo(2);
    }

    @Test
    void handlesNonAsciiCharacters() {
        assertThat(Solution.longestUniqueLength("\u00e9\u00e8\u00e9a")).isEqualTo(3);
    }

    @Test
    void handlesALongRepeatedPattern() {
        assertThat(Solution.longestUniqueLength("abc".repeat(10_000))).isEqualTo(3);
    }

    @Test
    void handlesALongAllDistinctRun() {
        StringBuilder text = new StringBuilder();
        for (char c = 'A'; c < 'A' + 5_000; c++) {
            text.append(c);
        }
        assertThat(Solution.longestUniqueLength(text.toString())).isEqualTo(5_000);
    }
}
