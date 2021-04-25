package com.jinloes.simple_functions.dynamic_programming;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UniquePathsTest {
    private UniquePaths uniquePaths;

    @Before
    public void setUp() throws Exception {
        uniquePaths = new UniquePaths();
    }

    @Test
    public void uniquePaths() {
        assertThat(uniquePaths.uniquePaths(3, 7))
                .isEqualTo(28);
    }

    @Test(timeout = 150)
    public void uniquePathsSlow() {
        // Takes 2mins + unoptimized
        assertThat(uniquePaths.uniquePaths(23, 12))
                .isEqualTo(193536720);

    }
}