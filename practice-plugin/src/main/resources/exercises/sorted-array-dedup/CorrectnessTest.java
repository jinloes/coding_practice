package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {

 @Test
    void keepsOneCopyWhenAllValuesMatch(){ assertPrefix(new int[]{7,7,7,7},1,new int[]{7}); }

 @Test
    void leavesAlreadyUniqueValuesInOrder(){ assertPrefix(new int[]{-5,-2,0,8},4,new int[]{-5,-2,0,8}); }

 @Test
    void handlesDuplicateRunsAtBothEnds(){ assertPrefix(new int[]{-2,-2,-2,0,1,1,1},3,new int[]{-2,0,1}); }

 @Test
    void handlesSingleElementArray(){ assertPrefix(new int[]{42},1,new int[]{42}); }

 @Test
    void compactsALargerPattern(){ int[] numbers=new int[60]; for(int i=0;i<numbers.length;i++) numbers[i]=i/6; assertPrefix(numbers,10,new int[]{0,1,2,3,4,5,6,7,8,9}); }

 private static void assertPrefix(int[] numbers,int expectedCount,int[] expectedPrefix){ int[] before=numbers.clone(); int count=Solution.deduplicate(numbers); assertThat(count).as("deduplicate(%s) count", Arrays.toString(before)).isEqualTo(expectedCount); assertThat(Arrays.copyOf(numbers,count)).as("deduplicate(%s) prefix", Arrays.toString(before)).containsExactly(expectedPrefix); }

}

