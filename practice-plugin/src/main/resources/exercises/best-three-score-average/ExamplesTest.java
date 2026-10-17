package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void choosesLargestTopThreeAverage() {
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Ada", 90), new Solution.Score("Ada", 95),
                new Solution.Score("Ada", 100), new Solution.Score("Bob", 100), new Solution.Score("Bob", 70),
                new Solution.Score("Bob", 60)))).isEqualTo("Ada");
    }

    @Test
    void ignoresStudentsWithTooFewScores() {
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Nia", 50), new Solution.Score("Nia", 80),
                new Solution.Score("Omar", 90)))).isEmpty();
    }

    @Test
    void breaksTiesByStudentName() {
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Bea", 85), new Solution.Score("Bea", 80),
                new Solution.Score("Bea", 75), new Solution.Score("Amy", 90), new Solution.Score("Amy", 80),
                new Solution.Score("Amy", 70)))).isEqualTo("Amy");
    }
}
