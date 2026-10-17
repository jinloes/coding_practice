package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void partitionsWhenPivotValueIsSmallest(){ assertPartition(new int[]{2,9,5,7},0); }
    @Test
    void partitionsWhenPivotValueIsLargest(){ assertPartition(new int[]{2,9,5,7},1); }
    @Test
    void partitionsWithManyEqualValues(){ assertPartition(new int[]{5,1,5,9,5,2,5},2); }
    @Test
    void partitionsNegativeValues(){ assertPartition(new int[]{0,-4,7,-4,3,-9},1); }
    @Test
    void partitionsLargeSeededPattern(){ int[] numbers=new int[101]; for(int i=0;i<numbers.length;i++) numbers[i]=(i*37)%11-5; assertPartition(numbers,50); } private static void assertPartition(int[] numbers,int pivotIndex){ int[] before=numbers.clone(); int pivot=numbers[pivotIndex]; Solution.partition(numbers,pivotIndex); assertThat(numbers).containsExactlyInAnyOrder(before); assertThat(signature(numbers,pivot)).as("partition(%s, %d)", Arrays.toString(before), pivotIndex).isSorted(); } private static int[] signature(int[] numbers,int pivot){ int[] s=new int[numbers.length]; for(int i=0;i<numbers.length;i++) s[i]=Integer.compare(numbers[i], pivot); return s; } }

