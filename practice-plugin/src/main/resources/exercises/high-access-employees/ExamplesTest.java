package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.List; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void findsAlice(){ assertThat(Solution.highAccessEmployees(List.of(a("alice","0830"),a("alice","0850"),a("alice","0920"),a("bob","1200")))).containsExactly("alice"); }
    @Test
    void exactHourDoesNotQualify(){ assertThat(Solution.highAccessEmployees(List.of(a("amy","1000"),a("amy","1030"),a("amy","1100")))).isEmpty(); }
    @Test
    void countsSameMinuteAccesses(){ assertThat(Solution.highAccessEmployees(List.of(a("zoe","0000"),a("zoe","0000"),a("zoe","0001")))).containsExactly("zoe"); } static Solution.Access a(String n,String t){ return new Solution.Access(n,t); } }
