package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleDigits() {
        check("9", "9", "81");
    }

    @Test
    void powersOfTen() {
        check("100", "100", "10000");
    }

    @Test
    void zeroTimesZero() {
        check("0", "0", "0");
    }

    @Test
    void identity() {
        check("99999", "1", "99999");
    }

    @Test
    void differentLengths() {
        check("5", "20", "100");
    }

    @Test
    void nineDigitProduct() {
        check("123456789", "987654321", "121932631112635269");
    }

    @Test
    void commutative() {
        check("4821", "37", "178377");
        check("37", "4821", "178377");
    }

    @Test
    void matchesBigIntegerOnLongNumbers() {
        String left = "7".repeat(60) + "13";
        String right = "9".repeat(45) + "2048";
        check(left, right, new BigInteger(left).multiply(new BigInteger(right)).toString());
    }

    private static void check(String left, String right, String expected) {
        assertThat(Solution.multiply(left, right)).as("multiply(\"%s\", \"%s\")".formatted(left, right)).isEqualTo(expected);
    }
}
