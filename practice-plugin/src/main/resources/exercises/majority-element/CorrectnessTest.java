package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void findsAMajorityThatFillsEveryEntry() {
        assertMajority(4, 4, 4, 4, 4);
    }

    @Test
    void findsAMajorityThatIsNotTheFirstValue() {
        assertMajority(5, 1, 5, 5);
        assertMajority(5, 1, 2, 5, 5, 5);
    }

    @Test
    void findsAMajorityThatIsNotTheMiddleValue() {
        assertMajority(9, 9, 9, 1, 2, 9);
    }

    @Test
    void findsAMajorityGroupedAtTheEnd() {
        assertMajority(8, 1, 2, 3, 8, 8, 8, 8);
    }

    @Test
    void findsAMajorityWithExactlyOneEntryToSpare() {
        assertMajority(6, 6, 1, 6, 2, 6, 3, 6);
        assertMajority(0, 1, 0, 0);
    }

    @Test
    void handlesNegativeAndExtremeValues() {
        assertMajority(-3, -3, 7, -3);
        assertMajority(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE);
        assertMajority(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, -1, Integer.MAX_VALUE);
    }

    @Test
    void handlesManyDistinctMinorityValues() {
        int size = 20_001;
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i < size / 2 ? i + 100 : 42;
        }
        assertMajority(42, numbers);
    }

    @Test
    void leavesTheArrayUnchanged() {
        int[] numbers = {2, 2, 1, 1, 1, 2, 2};
        int[] before = numbers.clone();
        majorityElement(numbers);
        assertThat(numbers).as("majorityElement must not mutate its input").containsExactly(before);
    }

    private static void assertMajority(int expected, int... numbers) {
        assertThat(majorityElement(numbers)).as(call(numbers)).isEqualTo(expected);
    }

    private static int majorityElement(int[] numbers) {
        try {
            return Solution.majorityElement(numbers);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(numbers), thrown), thrown);
        }
    }

    private static String call(int[] numbers) {
        String shown = numbers.length <= 12
                ? Arrays.toString(numbers)
                : "[" + numbers[0] + ", " + numbers[1] + ", ... (" + numbers.length + " values)]";
        return "majorityElement(%s)".formatted(shown);
    }
}
