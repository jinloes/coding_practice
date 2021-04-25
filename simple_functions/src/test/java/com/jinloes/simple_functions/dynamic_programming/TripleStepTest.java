package com.jinloes.simple_functions.dynamic_programming;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TripleStepTest {
    private TripleStep tripleStep;

    @Before
    public void setUp() throws Exception {
        tripleStep = new TripleStep();
    }

    @Test
    public void count() {
        assertThat(tripleStep.count(1)).isEqualTo(BigDecimal.valueOf(1));
        assertThat(tripleStep.count(2)).isEqualTo(BigDecimal.valueOf(2));
        assertThat(tripleStep.count(3)).isEqualTo(BigDecimal.valueOf(4));
        assertThat(tripleStep.count(5)).isEqualTo(BigDecimal.valueOf(13));

    }

    @Test(timeout = 100)
    public void countOverflow() {
        // Overflow condition, slow about a min unoptimized, ~50 ms optimized
        assertThat(tripleStep.count(37)).isEqualTo(new BigDecimal("3831006429"));
    }
}