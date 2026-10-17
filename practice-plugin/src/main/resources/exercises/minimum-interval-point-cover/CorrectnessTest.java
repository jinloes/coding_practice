package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNestedIntervals() { assertThat(Solution.minimumCoverPoints(intervals(0,10,2,3,4,5))).containsExactly(3,5); }
    @Test
    void handlesPointIntervals() { assertThat(Solution.minimumCoverPoints(intervals(4,4,1,4,4,7))).containsExactly(4); }
    @Test
    void startOrderWouldBeWrongForThisCase() { assertThat(Solution.minimumCoverPoints(intervals(1,10,2,3,4,5))).containsExactly(3,5); }
    @Test
    void doesNotMutateInputOrder() { Solution.Interval[] a=intervals(5,8,1,2,3,4); Solution.minimumCoverPoints(a); assertThat(a).containsExactly(new Solution.Interval(5,8), new Solution.Interval(1,2), new Solution.Interval(3,4)); }
    @Test
    void returnedPointsCoverEveryInterval() { Solution.Interval[] a=intervals(-5,-1,-3,2,0,4,7,9); int[] p=Solution.minimumCoverPoints(a); assertThat(p).containsExactly(-1,4,9); for(Solution.Interval in:a) assertThat(covered(in,p)).as("%s covered",in).isTrue(); }
    private static boolean covered(Solution.Interval in,int[] points){for(int p:points) if(in.start()<=p && p<=in.end()) return true; return false;}
    private static Solution.Interval[] intervals(int... v){Solution.Interval[] a=new Solution.Interval[v.length/2]; for(int i=0;i<a.length;i++)a[i]=new Solution.Interval(v[2*i],v[2*i+1]); return a;}
}
