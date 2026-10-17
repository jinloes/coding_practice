package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void findsOverlapWhenFirstOriginIsInsideSecond() { assertThat(Solution.intersection(rect(3,3,5,4), rect(0,0,5,5))).isEqualTo(rect(3,3,2,2)); }
    @Test
    void findsContainedRectangle() { assertThat(Solution.intersection(rect(0,0,10,10), rect(2,3,4,5))).isEqualTo(rect(2,3,4,5)); }
    @Test
    void returnsNullForSeparatedRectangles() { assertThat(Solution.intersection(rect(0,0,2,2), rect(3,3,2,2))).isNull(); }
    @Test
    void returnsNullForCornerTouch() { assertThat(Solution.intersection(rect(0,0,2,2), rect(2,2,1,1))).isNull(); }
    @Test
    void handlesLargeCoordinatesSafely() { assertThat(Solution.intersection(rect(Integer.MAX_VALUE-10,5,10,10), rect(Integer.MAX_VALUE-6,8,9,4))).isEqualTo(rect(Integer.MAX_VALUE-6,8,6,4)); }
    private static Solution.Rectangle rect(int x,int y,int w,int h){ return new Solution.Rectangle(x,y,w,h); }
}
