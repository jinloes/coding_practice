package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsEmptyForNegativeRows() { assertThat(Solution.rows(-3)).isEmpty(); }
    @Test
    void hasCorrectRowSizes() { List<List<Integer>> t=Solution.rows(8); assertThat(t).hasSize(8); for(int r=0;r<t.size();r++) assertThat(t.get(r)).as("row %d",r).hasSize(r+1); }
    @Test
    void computesMiddleValues() { assertThat(Solution.rows(7).get(6)).containsExactly(1,6,15,20,15,6,1); }
    @Test
    void everyInteriorCellAddsValuesAboveIt() { List<List<Integer>> t=Solution.rows(12); for(int r=2;r<t.size();r++) for(int c=1;c<r;c++) assertThat(t.get(r).get(c)).as("row %d column %d",r,c).isEqualTo(t.get(r-1).get(c-1)+t.get(r-1).get(c)); }
    @Test
    void laterRowsRemainVisible() { assertThat(Solution.rows(4).get(3)).containsExactly(1,3,3,1); }
}
