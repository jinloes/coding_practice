package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    @Test
    void readsEverySingleSymbol() {
        assertValue("I", 1);
        assertValue("V", 5);
        assertValue("X", 10);
        assertValue("L", 50);
        assertValue("C", 100);
        assertValue("D", 500);
        assertValue("M", 1000);
    }

    @Test
    void readsEverySubtractivePairOnItsOwn() {
        assertValue("IV", 4);
        assertValue("IX", 9);
        assertValue("XL", 40);
        assertValue("XC", 90);
        assertValue("CD", 400);
        assertValue("CM", 900);
    }

    @Test
    void addsASymbolAfterASubtractivePair() {
        assertValue("XIV", 14);
        assertValue("XLIX", 49);
        assertValue("CDXLIV", 444);
    }

    @Test
    void readsTheLargestValue() {
        assertValue("MMMCMXCIX", 3999);
    }

    @Test
    void readsTheLongestNumeral() {
        assertValue("MMMDCCCLXXXVIII", 3888);
    }

    @Test
    void readsNumeralsEndingInASubtractivePair() {
        assertValue("MMXXIV", 2024);
        assertValue("DCCCXC", 890);
    }

    @Test
    void readsEveryValueFromOneThrough3999() {
        for (int value = 1; value <= 3999; value++) {
            assertValue(toRoman(value), value);
        }
    }

    private static void assertValue(String numeral, int expected) {
        String call = "romanToInt(\"%s\")".formatted(numeral);
        int actual;
        try {
            actual = Solution.romanToInt(numeral);
        } catch (Throwable thrown) {
            fail("%s threw %s".formatted(call, thrown), thrown);
            return;
        }
        assertThat(actual).as(call).isEqualTo(expected);
    }

    private static String toRoman(int value) {
        StringBuilder numeral = new StringBuilder();
        int remaining = value;
        for (int i = 0; i < VALUES.length; i++) {
            while (remaining >= VALUES[i]) {
                numeral.append(SYMBOLS[i]);
                remaining -= VALUES[i];
            }
        }
        return numeral.toString();
    }
}
