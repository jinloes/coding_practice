package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsEveryDistinctTriplet() {
        check(new int[]{-4, 1, 3, 0, 4, -3}, new int[]{-4, 1, 3}, new int[]{-4, 0, 4}, new int[]{-3, 0, 3});
    }

    @Test
    void reportsRepeatedZerosOnce() {
        check(new int[]{0, 0, 0, 0}, new int[]{0, 0, 0});
    }

    @Test
    void reportsNoTripletWhenNoneExists() {
        check(new int[]{2, 5, 9});
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
