package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyFirstAndDuplicates() {
        int[] first={0,0,0}; Solution.merge(first,0,new int[]{-2,0,5},3); assertThat(first).startsWith(-2,0,5);
        first = new int[]{-4,-1,-1,0,0,0,0}; Solution.merge(first,3,new int[]{-4,-1,2,2},4); assertThat(first).startsWith(-4,-4,-1,-1,-1,2,2);
    }
    @Test
    void ignoresExtraCapacityAndHandlesLargeInput() {
        int[] first={2,8,0,0,99,100}; Solution.merge(first,2,new int[]{1,9},2); assertThat(first).startsWith(1,2,8,9);
        int m=500,n=500; first=new int[m+n]; int[] second=new int[n]; for(int i=0;i<m;i++) first[i]=i*2; for(int i=0;i<n;i++) second[i]=i*2+1;
        Solution.merge(first,m,second,n); for(int i=0;i<m+n;i++) assertThat(first[i]).as("index %d", i).isEqualTo(i);
    }
}

