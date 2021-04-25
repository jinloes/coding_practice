package com.jinloes.simple_functions.array;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxSlidingWindowTest {
    private MaxSlidingWindow maxSlidingWindow;

    @Before
    public void setUp() throws Exception {
        maxSlidingWindow = new MaxSlidingWindow();
    }

    @Test
    public void maxSlidingWindow() {
        assertThat(maxSlidingWindow.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3))
                .containsExactly(3, 3, 5, 5, 6, 7);

        assertThat(maxSlidingWindow.maxSlidingWindow(new int[]{9, 10, 9, -7, -4, -8, 2, -6}, 5))
                .containsExactly(10, 10, 9, 2);
    }
}