package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void parallelBranchesUseTheSlowestBranch(){ assertThat(Solution.minutesToInformAll(5,0,new int[]{-1,0,0,1,2},new int[]{2,4,1,0,0})).isEqualTo(6); }
    @Test
    void zeroDelayManagersDoNotAddTime(){ assertThat(Solution.minutesToInformAll(4,0,new int[]{-1,0,1,2},new int[]{0,0,0,0})).isZero(); }
    @Test
    void twoEmployeeTreeUsesHeadDelay(){ assertThat(Solution.minutesToInformAll(2,1,new int[]{1,-1},new int[]{0,3})).isEqualTo(3); }
    @Test
    void handlesLargeShallowOrganization(){ int n=200; int[] m=new int[n]; int[] t=new int[n]; m[0]=-1; t[0]=7; for(int i=1;i<n;i++) m[i]=0; assertThat(Solution.minutesToInformAll(n,0,m,t)).isEqualTo(7); } }
