package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void acceptsClearlySeparatedTeams(){ assertThat(Solution.canTakePhoto(new int[]{5,8,1},new int[]{6,9,2})).isTrue(); }
    @Test
    void rejectsWhenOnePairCannotBeSeparated(){ assertThat(Solution.canTakePhoto(new int[]{5,8,1},new int[]{6,7,2})).isFalse(); }
    @Test
    void rejectsWhenNeitherTeamCanBeEntirelyBehind(){ assertThat(Solution.canTakePhoto(new int[]{3,4},new int[]{4,3})).isFalse(); } }
