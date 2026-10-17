package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void keepsFirstAdaAndSortsNames() { assertThat(Solution.uniqueByFirstName(List.of(p("Ada","Lovelace"), p("Bob","Stone"), p("Ada","Byron")))).containsExactly(p("Ada","Lovelace"), p("Bob","Stone")); }
    @Test
    void sortsAfterRemovingDuplicateFirstNames() { assertThat(Solution.uniqueByFirstName(List.of(p("Zoe","Zed"), p("Amy","Able"), p("Zoe","Young")))).containsExactly(p("Amy","Able"), p("Zoe","Zed")); }
    @Test
    void handlesAnEmptyDirectory() { assertThat(Solution.uniqueByFirstName(List.of())).isEmpty(); }
    private static Solution.Person p(String first,String last){ return new Solution.Person(first,last); }
}
