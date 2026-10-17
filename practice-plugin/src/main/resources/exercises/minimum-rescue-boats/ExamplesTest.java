package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void pairsTwoPeopleThatFit() { assertThat(Solution.minBoats(new int[]{1, 2}, 3)).isEqualTo(1); }
    @Test
    void sendsHeavyPeopleAlone() { assertThat(Solution.minBoats(new int[]{3, 2, 2, 1}, 3)).isEqualTo(3); }
}
