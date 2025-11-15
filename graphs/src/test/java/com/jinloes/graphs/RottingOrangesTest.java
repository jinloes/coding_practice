package com.jinloes.graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RottingOrangesTest {
    private RottingOranges rottingOranges;

    @BeforeEach
    void setUp() {
        rottingOranges = new RottingOranges();
    }

    @Test
    void orangesRotting() {
        assertThat(rottingOranges.orangesRotting(
                new int[][]{
                        new int[]{1, 1, 0},
                        new int[]{0, 1, 1},
                        new int[]{0, 1, 2}
                }
        )).isEqualTo(4);
    }
}