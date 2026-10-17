package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {

 @Test
    void compactsSmallArray(){ assertPrefix(new int[]{1,1,2},2,new int[]{1,2}); }

 @Test
    void compactsNegativeAndPositiveValues(){ assertPrefix(new int[]{-3,-3,-1,0,0,5},4,new int[]{-3,-1,0,5}); }

 @Test
    void handlesEmptyArray(){ assertPrefix(new int[0],0,new int[0]); }

 private static void assertPrefix(int[] numbers,int expectedCount,int[] expectedPrefix){ int[] before=numbers.clone(); int count=Solution.deduplicate(numbers); assertThat(count).as("deduplicate(%s) count", Arrays.toString(before)).isEqualTo(expectedCount); assertThat(Arrays.copyOf(numbers,count)).as("deduplicate(%s) prefix", Arrays.toString(before)).containsExactly(expectedPrefix); }

}

