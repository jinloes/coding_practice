package com.jinloes.simple_functions;

import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class SorterTest {
    @Test
    public void test() {
        int[] arr = new Random().ints(25).toArray();
        Sorter.qsort(arr);
        assertThat(arr).isSorted();
    }
}
