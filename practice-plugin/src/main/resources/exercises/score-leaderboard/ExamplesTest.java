package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void sumsTopTwoPlayers() { Solution b = new Solution(); b.addScore(1, 50); b.addScore(2, 80); b.addScore(3, 20); assertThat(b.top(2)).isEqualTo(130); }
    @Test
    void addingScoreAccumulatesForSamePlayer() { Solution b = new Solution(); b.addScore(1, 50); b.addScore(2, 80); b.addScore(1, 25); assertThat(b.top(2)).isEqualTo(155); }
    @Test
    void resetRemovesPlayerFromTheBoard() { Solution b = new Solution(); b.addScore(1, 75); b.addScore(2, 80); b.addScore(3, 20); b.reset(2); assertThat(b.top(5)).isEqualTo(95); }
}
