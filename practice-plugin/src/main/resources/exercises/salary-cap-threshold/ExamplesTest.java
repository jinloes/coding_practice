package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void capsOnlyTheLargestSalaries(){ assertThat(Solution.findSalaryCap(new int[]{90,30,100,40},210)).isCloseTo(70.0, org.assertj.core.data.Offset.offset(1.0e-6)); }
    @Test
    void reportsImpossibleBudget(){ assertThat(Solution.findSalaryCap(new int[]{20,30},100)).isEqualTo(-1.0); }
    @Test
    void canCapEveryoneAtTheSameValue(){ assertThat(Solution.findSalaryCap(new int[]{10,20,30},30)).isCloseTo(10.0, org.assertj.core.data.Offset.offset(1.0e-6)); } }
