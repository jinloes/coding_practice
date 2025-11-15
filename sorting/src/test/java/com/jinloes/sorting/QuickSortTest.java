package com.jinloes.sorting;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class QuickSortTest {
    private static final Random RANDOM = new Random();

    @ParameterizedTest
    @MethodSource("sortArgs")
    public <T extends Comparable<? super T>> void sort(List<T> toSort) {
        List<T> copy = toSort == null ? null : new ArrayList<>(toSort);

        QuickSort.sort(toSort);

        if (toSort == null) {
            return;
        }
        assertThat(toSort)
                .isSorted();
        assertThat(toSort)
                .containsAll(copy);
    }

    @ParameterizedTest
    @MethodSource("sortArrayArgs")
    public void sort(int[] toSort) {
        int[] copy = toSort == null ? null : Arrays.copyOf(toSort, toSort.length);

        QuickSort.sortArray(toSort);

        if (toSort == null) {
            return;
        }

        assertThat(toSort)
                .isSorted();
        assertThat(toSort)
                .containsExactlyInAnyOrder(copy);
    }

    private static Stream<Arguments> sortArgs() {
        return Stream.of(
                Arguments.of((List<Integer>) null),
                Arguments.of(new ArrayList<>(List.of(10, 9, 1, 1, 1, 2, 3, 1))),
                Arguments.of(new ArrayList<>(List.of(5, 1, 3, 8, 10, 6))),
                Arguments.of(new ArrayList<>(RANDOM.ints(50)
                        .boxed()
                        .toList()))
        );
    }

    private static Stream<Arguments> sortArrayArgs() {
        return Stream.of(
                Arguments.of((int[]) null),
                Arguments.of(new int[]{10, 9, 1, 1, 1, 2, 3, 1}),
                Arguments.of(new int[]{5, 1, 3, 8, 10, 6}),
                Arguments.of(RANDOM.ints(50).toArray())
        );
    }
}