package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.ArrayList; import java.util.List; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void returnsNamesAlphabetically(){ assertThat(Solution.highAccessEmployees(List.of(a("zoe","0900"),a("ann","0900"),a("zoe","0910"),a("ann","0910"),a("zoe","0920"),a("ann","0920")))).containsExactly("ann","zoe"); }
    @Test
    void checksWindowsAfterSortingTimes(){ assertThat(Solution.highAccessEmployees(List.of(a("kai","1200"),a("kai","0900"),a("kai","0930"),a("kai","0959")))).containsExactly("kai"); }
    @Test
    void ignoresEmployeesWithOnlyTwoAccesses(){ assertThat(Solution.highAccessEmployees(List.of(a("min","0100"),a("min","0101")))).isEmpty(); }
    @Test
    void doesNotMutateInput(){ List<Solution.Access> logs=new ArrayList<>(List.of(a("a","0000"),a("a","0001"),a("a","0002"))); List<Solution.Access> before=List.copyOf(logs); Solution.highAccessEmployees(logs); assertThat(logs).containsExactlyElementsOf(before); }
    @Test
    void handlesSeededLargeLog(){ List<Solution.Access> logs=new ArrayList<>(); for(int i=0;i<300;i++) logs.add(a("emp"+(i%80), "%02d%02d".formatted((i/60)%24,i%60))); logs.add(a("target","1200")); logs.add(a("target","1215")); logs.add(a("target","1259")); assertThat(Solution.highAccessEmployees(logs)).contains("target"); } static Solution.Access a(String n,String t){ return new Solution.Access(n,t); } }
