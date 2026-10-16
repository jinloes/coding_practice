package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheLoneValueBetweenPairs() {
        assertThat(Solution.findUnpaired(new int[]{4, 9, 4, 7, 9})).as("findUnpaired of [4, 9, 4, 7, 9]").isEqualTo(7);
    }

    @Test
    void singleElementIsUnpaired() {
        assertThat(Solution.findUnpaired(new int[]{42})).as("findUnpaired of [42]").isEqualTo(42);
    }

    @Test
    void negativeValuesWork() {
        assertThat(Solution.findUnpaired(new int[]{-5, 3, -5})).as("findUnpaired of [-5, 3, -5]").isEqualTo(3);
    }

}
