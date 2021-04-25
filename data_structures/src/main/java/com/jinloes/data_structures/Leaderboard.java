package com.jinloes.data_structures;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Model a leaderboard.
 */
public class Leaderboard {
    private final Map<String, Integer> leaderBoard;

    public Leaderboard() {
        this.leaderBoard = new HashMap<>();
    }

    public void addPlayer(String name) {
        leaderBoard.putIfAbsent(name, 0);
    }

    public void update(String name, int newScore) {
        leaderBoard.put(name, newScore);
    }

    public List<LeaderBoardRecord> getView() {
        return Collections.unmodifiableList(leaderBoard.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map((Function<Map.Entry<String, Integer>, LeaderBoardRecord>) input ->
                        new LeaderBoardRecord(input.getKey(), input.getValue()))
                .collect(Collectors.toList()));
    }

    public static class LeaderBoardRecord {
        private final String name;
        private final int score;

        public LeaderBoardRecord(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LeaderBoardRecord that = (LeaderBoardRecord) o;
            return score == that.score &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, score);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name)
                    .add("score", score)
                    .toString();
        }
    }
}