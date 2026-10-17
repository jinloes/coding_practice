package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void prefersSmallerOnTies(){ assertThat(Solution.closestValues(new int[]{1,2,3,4,5},4,3)).containsExactly(1,2,3,4); }
    @Test
    void targetBetweenValues(){ assertThat(Solution.closestValues(new int[]{1,3,6,10},2,7)).containsExactly(6,10); } }



