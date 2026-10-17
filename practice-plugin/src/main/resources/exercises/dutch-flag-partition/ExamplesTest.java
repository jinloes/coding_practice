package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void partitionsMixedValues(){ assertPartition(new int[]{3,5,2,3,8},0); }
    @Test
    void handlesAllEqualValues(){ int[] numbers={4,4,4}; Solution.partition(numbers,1); assertThat(numbers).containsExactly(4,4,4); }
    @Test
    void handlesEmptyInput(){ int[] numbers={}; Solution.partition(numbers,0); assertThat(numbers).isEmpty(); } private static void assertPartition(int[] numbers,int pivotIndex){ int[] before=numbers.clone(); int pivot=numbers[pivotIndex]; Solution.partition(numbers,pivotIndex); assertThat(numbers).containsExactlyInAnyOrder(before); assertThat(signature(numbers,pivot)).as("partition(%s, %d)", Arrays.toString(before), pivotIndex).isSorted(); } private static int[] signature(int[] numbers,int pivot){ int[] s=new int[numbers.length]; for(int i=0;i<numbers.length;i++) s[i]=Integer.compare(numbers[i], pivot); return s; } }

