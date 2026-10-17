package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void ignoresUnknownDecrementsAndAcceptsTies() { Solution t = new Solution(); t.decreasePopularity(99); assertThat(t.mostPopular()).isEqualTo(-1); t.increasePopularity(1); t.increasePopularity(2); assertThat(t.mostPopular()).isIn(1, 2); }
    @Test
    void followsChangingMaximum() { Solution t = new Solution(); for (int id : new int[]{1,1,2,2,2,3,3,3}) t.increasePopularity(id); assertThat(t.mostPopular()).isIn(2,3); t.decreasePopularity(2); t.decreasePopularity(3); assertThat(t.mostPopular()).isIn(1,2,3); t.decreasePopularity(1); t.decreasePopularity(1); assertThat(t.mostPopular()).isIn(2,3); }
}
