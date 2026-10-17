package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void mergesSeveralRuns(){ assertThat(Solution.sortAlternatingRuns(new int[]{1,4,9,8,3,2,6,7})).containsExactly(1,2,3,4,6,7,8,9); }
    @Test
    void reversesDecreasingRun(){ assertThat(Solution.sortAlternatingRuns(new int[]{5,4,4,1})).containsExactly(1,4,4,5); } }


