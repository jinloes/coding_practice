package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndNonPositiveTopReturnZero() { Solution b = new Solution(); assertThat(b.top(3)).isZero(); assertThat(b.top(0)).isZero(); assertThat(b.top(-2)).isZero(); }
    @Test
    void resetMissingPlayerDoesNothing() { Solution b = new Solution(); b.addScore(1, 10); b.reset(2); assertThat(b.top(1)).isEqualTo(10); }
    @Test
    void handlesTiesBySumOnly() { Solution b = new Solution(); b.addScore(1, 10); b.addScore(2, 10); b.addScore(3, 5); assertThat(b.top(2)).isEqualTo(20); }
    @Test
    void negativeAdjustmentsAreIncluded() { Solution b = new Solution(); b.addScore(1, 20); b.addScore(1, -5); b.addScore(2, 10); assertThat(b.top(2)).isEqualTo(25); }
    @Test
    void matchesOracleForSeededOperations() {
        Solution b = new Solution(); Map<Integer, Integer> oracle = new HashMap<>(); Random random = new Random(44);
        for (int step = 0; step < 500; step++) {
            int player = random.nextInt(30);
            if (random.nextInt(5) == 0) { b.reset(player); oracle.remove(player); }
            else { int score = random.nextInt(40) - 5; b.addScore(player, score); oracle.merge(player, score, Integer::sum); }
            int k = random.nextInt(35);
            int expected = oracle.values().stream().sorted((a, c) -> Integer.compare(c, a)).limit(k).mapToInt(Integer::intValue).sum();
            assertThat(b.top(k)).as("step %d top(%d)", step, k).isEqualTo(expected);
        }
    }
}
