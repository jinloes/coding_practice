package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void parsesPositiveAndNegativeBoundaries() { assertThat(Solution.stringToInt("2147483647")).isEqualTo(Integer.MAX_VALUE); assertThat(Solution.stringToInt("-2147483648")).isEqualTo(Integer.MIN_VALUE); }
    @Test
    void formatsZeroAndNegativeValues() { assertThat(Solution.intToString(0)).isEqualTo("0"); assertThat(Solution.intToString(-987654321)).isEqualTo("-987654321"); }
    @Test
    void formatsMostNegativeInteger() { assertThat(Solution.intToString(Integer.MIN_VALUE)).isEqualTo("-2147483648"); }
    @Test
    void roundTripsGeneratedValues() { for (int i = -100000; i <= 100000; i += 7919) assertThat(Solution.stringToInt(Solution.intToString(i))).isEqualTo(i); }
}
