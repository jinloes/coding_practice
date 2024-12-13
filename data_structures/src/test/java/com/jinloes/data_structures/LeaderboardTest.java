package com.jinloes.data_structures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaderboardTest {
    private Leaderboard leaderboard;

    @BeforeEach
    public void setUp() throws Exception {
        leaderboard = new Leaderboard();
    }

    @Test
    public void addPlayer() {
        leaderboard.addPlayer("p1");

        assertThat(leaderboard.getView())
                .containsExactly(new Leaderboard.LeaderBoardRecord("p1", 0));

        leaderboard.addPlayer("p2");

        assertThat(leaderboard.getView()).containsExactly(new Leaderboard.LeaderBoardRecord("p1", 0),
                new Leaderboard.LeaderBoardRecord("p2", 0));

        leaderboard.addPlayer("p3");

        assertThat(leaderboard.getView()).containsExactly(new Leaderboard.LeaderBoardRecord("p1", 0),
                new Leaderboard.LeaderBoardRecord("p2", 0),
                new Leaderboard.LeaderBoardRecord("p3", 0));
    }

    @Test
    public void update() {
        leaderboard.update("p1", 5);
        leaderboard.update("p2", 1);
        leaderboard.update("p3", 10);

        assertThat(leaderboard.getView()).containsExactly(new Leaderboard.LeaderBoardRecord("p3", 10),
                new Leaderboard.LeaderBoardRecord("p1", 5),
                new Leaderboard.LeaderBoardRecord("p2", 1));

        leaderboard.update("p2", 100);

        assertThat(leaderboard.getView()).containsExactly(new Leaderboard.LeaderBoardRecord("p2", 100),
                new Leaderboard.LeaderBoardRecord("p3", 10),
                new Leaderboard.LeaderBoardRecord("p1", 5));
    }
}