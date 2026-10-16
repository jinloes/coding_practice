package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void multipliesSmallNumbers() {
        check("12", "13", "156");
    }

    @Test
    void carriesAcrossDigits() {
        check("999", "999", "998001");
    }

    @Test
    void zeroTimesAnything() {
        check("0", "5", "0");
    }

    private static void check(String left, String right, String expected) {
        assertThat(Solution.multiply(left, right)).as("multiply(\"%s\", \"%s\")".formatted(left, right)).isEqualTo(expected);
    }
}
