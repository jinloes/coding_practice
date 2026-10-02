package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndShortInputs() {
        check(new int[0]);
        check(new int[]{0});
        check(new int[]{0, 0});
    }

    @Test
    void acceptsExactlyThreeValues() {
        check(new int[]{3, -1, -2}, new int[]{-2, -1, 3});
        check(new int[]{1, 2, 3});
    }

    @Test
    void doesNotRepeatTriplets() {
        check(new int[]{-1, -1, -1, 2, 2, 0, 1, 1}, new int[]{-1, -1, 2}, new int[]{-1, 0, 1});
    }

    @Test
    void rejectsTriplesWithoutThreeDistinctPositions() {
        check(new int[]{-2, 1, 5});
    }

    @Test
    void rejectsAllPositiveValues() {
        check(new int[]{1, 2, 3, 4, 5});
    }

    @Test
    void usesTwoCopiesOfAValueOnlyWhenItAppearsTwice() {
        check(new int[]{4, -2, -2}, new int[]{4, -2, -2});
        check(new int[]{4, -2, 5});
    }

    @Test
    void doesNotBeFooledByIntOverflow() {
        check(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, 0});
    }

    @Test
    void findsTripletsAtIntegerExtremes() {
        check(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 7}, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 1});
    }

    @Test
    void handlesNegativeOnlyInput() {
        check(new int[]{-5, -3, -1});
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {3, -1, -2, 0};
        int[] before = numbers.clone();
        Solution.findTriplets(numbers);
        assertThat(numbers).as("findTriplets must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] numbers, int[]... expected) {
        String call = "findTriplets(%s)".formatted(Arrays.toString(numbers));
        List<List<Integer>> actual = Solution.findTriplets(numbers);
        assertThat(actual).as(call).isNotNull();
        assertThat(canon(actual)).as(call + " canonical triplets").isEqualTo(canon(expected));
    }

    private static List<String> canon(List<List<Integer>> triplets) {
        return triplets.stream()
                .map(triplet -> triplet.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")))
                .sorted()
                .toList();
    }

    private static List<String> canon(int[][] triplets) {
        return Arrays.stream(triplets)
                .map(triplet -> Arrays.stream(triplet).sorted().mapToObj(String::valueOf).collect(Collectors.joining(",")))
                .sorted()
                .toList();
    }
}
