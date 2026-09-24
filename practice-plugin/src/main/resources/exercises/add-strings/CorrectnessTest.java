package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void addsZeroOnEitherSide() {
        assertSum("0", "58", "58");
        assertSum("58", "0", "58");
    }

    @Test
    void addsSingleDigitsWithAndWithoutACarry() {
        assertSum("2", "3", "5");
        assertSum("5", "5", "10");
        assertSum("9", "9", "18");
    }

    @Test
    void addsWithoutAnyCarry() {
        assertSum("1234", "4321", "5555");
    }

    @Test
    void carriesThroughALongChainOfNines() {
        assertSum("999999999", "1", "1000000000");
        assertSum("1", "999999999", "1000000000");
    }

    @Test
    void carriesOnlyInTheMiddle() {
        assertSum("1050", "1050", "2100");
    }

    @Test
    void handlesTheShorterNumberOnEitherSide() {
        assertSum("7", "123456", "123463");
        assertSum("123456", "7", "123463");
    }

    @Test
    void addsNumbersBeyondTheRangeOfALong() {
        assertSum("9223372036854775807", "9223372036854775807", "18446744073709551614");
    }

    @Test
    void addsNumbersWithThousandsOfDigits() {
        StringBuilder first = new StringBuilder("7");
        StringBuilder second = new StringBuilder("3");
        for (int i = 0; i < 4_000; i++) {
            first.append((char) ('0' + i % 10));
            second.append((char) ('0' + (i * 7) % 10));
        }
        String expected = new BigInteger(first.toString()).add(new BigInteger(second.toString())).toString();
        assertSum(first.toString(), second.toString(), expected);
    }

    private static void assertSum(String first, String second, String expected) {
        String call = call(first, second);
        String actual;
        try {
            actual = Solution.addStrings(first, second);
        } catch (Throwable thrown) {
            fail("%s threw %s".formatted(call, thrown), thrown);
            return;
        }
        assertThat(actual).as("%s must return the decimal sum", call).isEqualTo(expected);
    }

    private static String call(String first, String second) {
        return "addStrings(\"%s\", \"%s\")".formatted(abbreviate(first), abbreviate(second));
    }

    private static String abbreviate(String number) {
        return number.length() <= 30 ? number : number.substring(0, 30) + "... (" + number.length() + " digits)";
    }
}
