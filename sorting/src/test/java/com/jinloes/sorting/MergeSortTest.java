package com.jinloes.sorting;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MergeSortTest {
    private static final Random RANDOM = new Random();

    @Test
    public void sort() {
        List<Integer> toSort = Lists.newArrayList(5, 1, 3, 8, 10, 6);
        List<Integer> copy = new ArrayList<>(toSort);

        assertThat(MergeSort.sort(toSort)).isSorted();
        assertThat(toSort).containsAll(copy);

        assertThat(MergeSort.sort(null)).isNull();
    }

    @Test
    public void sortRandom() {
        List<Integer> toSort = RANDOM.ints(50)
                .boxed()
                .collect(Collectors.toList());
        List<Integer> copy = new ArrayList<>(toSort);

        assertThat(MergeSort.sort(toSort)).isSorted();
        assertThat(toSort).containsAll(copy);
    }
}