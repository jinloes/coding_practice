package com.jinloes.simple_functions.dynamic_programming;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxScoreMultiOperationsTest {
    private MaxScoreMultiOperations maxScoreMultiOperations;

    @Before
    public void setUp() throws Exception {
        maxScoreMultiOperations = new MaxScoreMultiOperations();
    }

    @Test
    public void maximumScore() {
        assertThat(maxScoreMultiOperations.maximumScore(new int[]{1, 2, 3}, new int[]{3, 2, 1}))
                .isEqualTo(14);
    }

    @Test
    public void maximumScoreMedium() {
        assertThat(maxScoreMultiOperations.maximumScore(new int[]{-5, -3, -3, -2, 7, 1}, new int[]{-10, -5, 3, 4, 6}))
                .isEqualTo(102);
    }
}