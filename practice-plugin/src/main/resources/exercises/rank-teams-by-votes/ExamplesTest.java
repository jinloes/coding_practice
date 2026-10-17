package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void ranksByPositionCounts(){ assertThat(Solution.rankTeams(new String[]{"ABC","ACB","ABC","ACB","ACB"})).isEqualTo("ACB"); }
    @Test
    void ranksByNextPositionCounts(){ assertThat(Solution.rankTeams(new String[]{"WXYZ","XYZW"})).isEqualTo("XWYZ"); }
    @Test
    void handlesNoVotes(){ assertThat(Solution.rankTeams(new String[0])).isEmpty(); } }
