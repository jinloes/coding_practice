package com.jinloes.simple_functions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberCombinationTest {

    @Test
    void findCombo_exists() {
        assertThat(NumberCombination.findCombo(new int[]{15, 5, 3, 1}, 8)).isTrue();
        assertThat(NumberCombination.findCombo(new int[]{15, 5, 3, 1}, 9)).isTrue();
    }

    @Test
    void findCombo_doesNotExist() {
        assertThat(NumberCombination.findCombo(new int[]{15, 5, 3, 1}, 10)).isFalse();
        assertThat(NumberCombination.findCombo(new int[]{15, 5, 3, 2}, 1)).isFalse();
        assertThat(NumberCombination.findCombo(new int[]{15, 5, 3, 1}, 17)).isFalse();
    }
}