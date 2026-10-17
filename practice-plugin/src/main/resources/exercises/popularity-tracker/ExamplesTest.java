package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsTheOnlyHighestId() { Solution tracker = new Solution(); tracker.increasePopularity(10); tracker.increasePopularity(20); tracker.increasePopularity(10); assertThat(tracker.mostPopular()).isEqualTo(10); }
    @Test
    void removesIdsThatFallToZero() { Solution tracker = new Solution(); tracker.increasePopularity(4); tracker.decreasePopularity(4); assertThat(tracker.mostPopular()).isEqualTo(-1); }
}
