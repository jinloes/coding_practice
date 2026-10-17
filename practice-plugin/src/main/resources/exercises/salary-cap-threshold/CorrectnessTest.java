package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.Arrays; import java.util.Random; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void returnsFullSalaryCapWhenTargetEqualsPayroll(){ assertThat(Solution.findSalaryCap(new int[]{15,25,35},75)).isCloseTo(35.0, off()); }
    @Test
    void handlesZeroTarget(){ assertThat(Solution.findSalaryCap(new int[]{15,25,35},0)).isCloseTo(0.0, off()); }
    @Test
    void handlesEmptyPayroll(){ assertThat(Solution.findSalaryCap(new int[0],0)).isCloseTo(0.0, off()); assertThat(Solution.findSalaryCap(new int[0],1)).isEqualTo(-1.0); }
    @Test
    void doesNotMutateInput(){ int[] s={80,10,70,30}; int[] b=s.clone(); Solution.findSalaryCap(s,120); assertThat(s).containsExactly(b); }
    @Test
    void handlesFractionalCap(){ assertThat(Solution.findSalaryCap(new int[]{100,100,1},150)).isCloseTo(74.5, off()); }
    @Test
    void matchesBudgetForSeededLargePayroll(){ Random random=new Random(9); int[] s=new int[200]; for(int i=0;i<s.length;i++)s[i]=50+random.nextInt(500); double target=Arrays.stream(s).sum()*0.62; double cap=Solution.findSalaryCap(s,target); assertThat(payroll(s,cap)).isCloseTo(target, org.assertj.core.data.Offset.offset(1.0e-4)); } static double payroll(int[] s,double cap){ double total=0; for(int v:s) total+=Math.min(v,cap); return total; } static org.assertj.core.data.Offset<Double> off(){ return org.assertj.core.data.Offset.offset(1.0e-6); } }
