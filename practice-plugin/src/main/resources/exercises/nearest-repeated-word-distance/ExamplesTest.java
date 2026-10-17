package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsDistanceBetweenClosestRepeatedWords() { assertThat(Solution.nearestDistance(new String[]{"all","work","and","no","play","makes","for","no","fun"})).isEqualTo(4); }
    @Test
    void returnsMinusOneWhenNoWordRepeats() { assertThat(Solution.nearestDistance(new String[]{"a","b","c"})).isEqualTo(-1); }
    @Test
    void adjacentRepeatHasDistanceOne() { assertThat(Solution.nearestDistance(new String[]{"x","y","x","x"})).isEqualTo(1); }
}
