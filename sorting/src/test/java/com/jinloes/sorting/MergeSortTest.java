package com.jinloes.sorting;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MergeSortTest {

    @Test
    void mergeSort() {
        List<Integer> arr = Arrays.asList(3, 1, 6, 2);
        List<Integer> expected = Arrays.asList(3, 1, 6, 2);
        expected.sort(Integer::compareTo);
        assertThat(Sorter.mergeSort(arr)).isEqualTo(expected);
    }
}