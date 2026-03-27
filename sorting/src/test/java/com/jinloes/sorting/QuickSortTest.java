package com.jinloes.sorting;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class QuickSortTest {

    @Test
    void quickSort() {
        int[] arr = {3, 1, 2};
        int[] expected = arr.clone();
        Arrays.sort(expected);
        Sorter.quickSort(arr);
        assertThat(arr).isEqualTo(expected);
    }

    @Test
    void quickSort_nullArray() {
        Sorter.quickSort(null);
    }

    @Test
    void quickSort_emptyArray() {
        int[] arr = {};
        Sorter.quickSort(arr);
        assertThat(arr).isEmpty();
    }
}