package com.jinloes.simple_functions;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class SorterTest {
    @Test
    public void test() {
        int[] arr = new Random().ints(25).toArray();
        Sorter.qsort(arr);
        assertThat(arr).isSorted();
    }

    @Test
    public void testMSort() {
        int[] arr = new Random().ints(25).toArray();
        List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
        assertThat(Sorter.mSort(list)).isSorted();
    }
}
