package com.jinloes.sorting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RankTeamsTest {
    private RankTeams rankTeams;

    @BeforeEach
    void setUp() {
        rankTeams = new RankTeams();
    }

    @Test
    void rankTeams() {
        assertThat(rankTeams.rankTeams(new String[]{"ABC", "ACB", "ABC", "ACB", "ACB"}))
                .isEqualTo("ACB");
    }
}