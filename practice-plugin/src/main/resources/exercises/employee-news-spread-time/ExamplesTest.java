package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void singleEmployeeAlreadyKnows(){ assertThat(Solution.minutesToInformAll(1,0,new int[]{-1},new int[]{0})).isZero(); }
    @Test
    void starManagementTreeUsesHeadDelay(){ assertThat(Solution.minutesToInformAll(6,2,new int[]{2,2,-1,2,2,2},new int[]{0,0,1,0,0,0})).isEqualTo(1); }
    @Test
    void chainAddsEveryManagerDelay(){ assertThat(Solution.minutesToInformAll(7,6,new int[]{1,2,3,4,5,6,-1},new int[]{0,6,5,4,3,2,1})).isEqualTo(21); } }
