package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void usesAlphabeticalTieBreaker(){ assertThat(Solution.rankTeams(new String[]{"BA","AB"})).isEqualTo("AB"); }
    @Test
    void handlesSingleVote(){ assertThat(Solution.rankTeams(new String[]{"ZMN"})).isEqualTo("ZMN"); }
    @Test
    void comparesAllPositions(){ assertThat(Solution.rankTeams(new String[]{"ABC","BAC","BCA","ACB"})).isEqualTo("ABC"); }
    @Test
    void handlesManySeededVotes(){ String[] votes=new String[200]; for(int i=0;i<votes.length;i++) votes[i]= i%3==0?"CBA":(i%3==1?"BAC":"ABC"); String result=Solution.rankTeams(votes); assertThat(result).hasSize(3); assertThat(result.chars().mapToObj(c -> (char) c).toList()).containsOnlyOnce('A','B','C'); }
    @Test
    void doesNotMutateVotes(){ String[] votes={"ABC","ACB"}; String[] before=votes.clone(); Solution.rankTeams(votes); assertThat(votes).containsExactly(before); } }
