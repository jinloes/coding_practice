package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void returnsZeroForAnEmptyArray() {
        assertMissing(0);
    }

    @Test
    void handlesASingleValue() {
        assertMissing(1, 0);
        assertMissing(0, 1);
    }

    @Test
    void findsAMissingZero() {
        assertMissing(0, 4, 2, 3, 1);
    }

    @Test
    void findsAMissingLargestValueInReverseOrder() {
        assertMissing(5, 4, 3, 2, 1, 0);
    }

    @Test
    void findsEveryPossibleGapInASmallRange() {
        for (int gap = 0; gap <= 8; gap++) {
            int[] numbers = new int[8];
            int next = 0;
            for (int value = 8; value >= 0; value--) {
                if (value != gap) {
                    numbers[next++] = value;
                }
            }
            assertMissing(gap, numbers);
        }
    }

    @Test
    void handlesALargeShuffledRange() {
        int size = 100_000;
        int gap = 61_803;
        int[] numbers = new int[size];
        int next = 0;
        for (int value = 0; value <= size; value++) {
            if (value != gap) {
                numbers[next++] = value;
            }
        }
        for (int i = 0; i < size; i++) {
            int j = (int) ((i * 2_654_435_761L) % size);
            int held = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = held;
        }
        assertMissing(gap, numbers);
    }

    @Test
    void leavesTheArrayUnchanged() {
        int[] numbers = {9, 6, 4, 2, 3, 5, 7, 0, 1};
        int[] before = numbers.clone();
        missingNumber(numbers);
        assertThat(numbers).as("missingNumber must not mutate its input").containsExactly(before);
    }

    private static void assertMissing(int expected, int... numbers) {
        assertThat(missingNumber(numbers)).as(call(numbers)).isEqualTo(expected);
    }

    private static int missingNumber(int[] numbers) {
        try {
            return Solution.missingNumber(numbers);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(numbers), thrown), thrown);
        }
    }

    private static String call(int[] numbers) {
        String shown = numbers.length <= 12
                ? Arrays.toString(numbers)
                : "[" + numbers[0] + ", " + numbers[1] + ", ... (" + numbers.length + " values)]";
        return "missingNumber(%s)".formatted(shown);
    }
}
