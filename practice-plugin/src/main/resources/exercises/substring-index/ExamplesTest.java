package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsPrefix() {
        assertThat(Solution.substringIndex("abc123", "abc")).isEqualTo(0);
    }

    @Test
    void findsMiddle() {
        assertThat(Solution.substringIndex("abc123", "c12")).isEqualTo(2);
    }

    @Test
    void reportsMissingPattern() {
        assertThat(Solution.substringIndex("abc123", "xyz")).isEqualTo(-1);
    }
}
