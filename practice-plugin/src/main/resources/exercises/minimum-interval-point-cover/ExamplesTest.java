package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void coversSeparatedGroups() { assertThat(Solution.minimumCoverPoints(intervals(1,3,2,5,6,7))).containsExactly(3,7); }
    @Test
    void treatsEndpointsAsInclusive() { assertThat(Solution.minimumCoverPoints(intervals(1,2,2,3,3,4))).containsExactly(2,4); }
    @Test
    void handlesNoIntervals() { assertThat(Solution.minimumCoverPoints(new Solution.Interval[0])).isEmpty(); }
    private static Solution.Interval[] intervals(int... v){Solution.Interval[] a=new Solution.Interval[v.length/2]; for(int i=0;i<a.length;i++)a[i]=new Solution.Interval(v[2*i],v[2*i+1]); return a;}
}
