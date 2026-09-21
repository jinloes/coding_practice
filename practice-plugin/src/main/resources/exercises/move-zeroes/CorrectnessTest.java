package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void handlesAnEmptyArray() {
        assertRearranged(new int[0]);
    }

    @Test
    void handlesSingleValueArrays() {
        assertRearranged(new int[]{0}, 0);
        assertRearranged(new int[]{7}, 7);
    }

    @Test
    void movesALeadingRunOfZeroes() {
        assertRearranged(new int[]{0, 0, 0, 4, 5}, 4, 5, 0, 0, 0);
    }

    @Test
    void leavesATrailingRunOfZeroesInPlace() {
        assertRearranged(new int[]{4, 5, 0, 0, 0}, 4, 5, 0, 0, 0);
    }

    @Test
    void keepsNegativeValuesAndDuplicatesInOrder() {
        assertRearranged(new int[]{0, -3, 0, -3, 2, 0, 2}, -3, -3, 2, 2, 0, 0, 0);
    }

    @Test
    void handlesAlternatingZeroes() {
        assertRearranged(new int[]{1, 0, 2, 0, 3, 0}, 1, 2, 3, 0, 0, 0);
    }

    @Test
    void preservesTheOrderOfAZeroFreeArray() {
        assertRearranged(new int[]{9, -1, 4, 4, -1}, 9, -1, 4, 4, -1);
    }

    @Test
    void handlesALongAlternatingArray() {
        int[] numbers = new int[10_000];
        int[] expected = new int[numbers.length];
        for (int i = 0; i < numbers.length; i += 2) {
            numbers[i] = 0;
            numbers[i + 1] = i + 1;
            expected[i / 2] = i + 1;
        }
        assertRearranged(numbers, expected);
    }

    private static void assertRearranged(int[] numbers, int... expected) {
        String call = call(numbers);
        rearrange(numbers, call);
        assertThat(numbers).as("%s must leave %s", call, Arrays.toString(expected))
                .containsExactly(expected);
    }

    private static void rearrange(int[] numbers, String call) {
        try {
            Solution.moveZeroes(numbers);
        } catch (Throwable thrown) {
            fail("%s threw %s".formatted(call, thrown), thrown);
        }
    }

    private static String call(int[] numbers) {
        String shown = numbers.length <= 12
                ? Arrays.toString(numbers)
                : "[" + numbers[0] + ", " + numbers[1] + ", ... (" + numbers.length + " values)]";
        return "moveZeroes(%s)".formatted(shown);
    }
}
