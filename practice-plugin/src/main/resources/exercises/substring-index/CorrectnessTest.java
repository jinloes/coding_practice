package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNullAndEmptyInputs() {
        assertThat(Solution.substringIndex(null, "abc")).isEqualTo(-1);
        assertThat(Solution.substringIndex("abc", null)).isEqualTo(-1);
        assertThat(Solution.substringIndex(null, null)).isEqualTo(-1);
        assertThat(Solution.substringIndex("abc", "")).isEqualTo(0);
        assertThat(Solution.substringIndex("", "")).isEqualTo(0);
        assertThat(Solution.substringIndex("", "a")).isEqualTo(-1);
    }

    @Test
    void findsOverlappingPatternAfterPartialMismatch() {
        assertThat(Solution.substringIndex("aaab", "aab")).isEqualTo(1);
        assertThat(Solution.substringIndex("ababaca", "abaca")).isEqualTo(2);
    }

    @Test
    void returnsFirstMatchWhenPatternRepeats() {
        assertThat(Solution.substringIndex("abcabc", "abc")).isEqualTo(0);
        assertThat(Solution.substringIndex("mississippi", "issi")).isEqualTo(1);
    }

    @Test
    void matchesJavaIndexOfOnSeededData() {
        Random random = new Random(42);
        String alphabet = "abcd";
        for (int trial = 0; trial < 200; trial++) {
            int length = 40 + random.nextInt(40);
            StringBuilder text = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                text.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            int start = random.nextInt(length - 3);
            int patternLength = 1 + random.nextInt(6);
            String pattern = text.substring(start, Math.min(length, start + patternLength));
            assertThat(Solution.substringIndex(text.toString(), pattern))
                    .as("first index of %s in %s", pattern, text)
                    .isEqualTo(text.indexOf(pattern));
        }
    }
}
