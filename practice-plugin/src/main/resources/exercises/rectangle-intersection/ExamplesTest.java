package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsInteriorOverlap() { assertThat(Solution.intersection(rect(0,0,4,3), rect(2,1,4,4))).isEqualTo(rect(2,1,2,2)); }
    @Test
    void touchingEdgesAreNotAnIntersection() { assertThat(Solution.intersection(rect(0,0,2,2), rect(2,0,2,2))).isNull(); }
    @Test
    void supportsNegativeCoordinates() { assertThat(Solution.intersection(rect(-3,-2,4,4), rect(-1,-5,3,5))).isEqualTo(rect(-1,-2,2,2)); }
    private static Solution.Rectangle rect(int x,int y,int w,int h){ return new Solution.Rectangle(x,y,w,h); }
}
