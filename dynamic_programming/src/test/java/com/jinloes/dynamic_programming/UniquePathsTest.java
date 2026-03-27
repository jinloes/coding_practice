package com.jinloes.dynamic_programming;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.assertj.core.api.Assertions.assertThat;

public class UniquePathsTest {
    private UniquePaths uniquePaths;

    @BeforeEach
    public void setUp() throws Exception {
        uniquePaths = new UniquePaths();
    }

    @Test
    public void uniquePaths() {
        assertThat(uniquePaths.uniquePaths(3, 7))
                .isEqualTo(28);
    }

    @Test
    @Timeout(value = 150, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
    public void uniquePathsSlow() {
        // Takes 2mins + unoptimized
        assertThat(uniquePaths.uniquePaths(23, 12))
                .isEqualTo(193536720);

    }
}