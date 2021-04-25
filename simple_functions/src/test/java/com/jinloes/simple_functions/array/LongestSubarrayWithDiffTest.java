package com.jinloes.simple_functions.array;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestSubarrayWithDiffTest {
    private LongestSubarrayWithDiff longestSubarray;

    @Before
    public void setUp() throws Exception {
        longestSubarray = new LongestSubarrayWithDiff();
    }

    @Test
    public void longestSubarray() {
        assertThat(longestSubarray.longestSubarray(new int[]{8, 2, 4, 7}, 4))
                .isEqualTo(2);
        assertThat(longestSubarray.longestSubarray(new int[]{10, 1, 2, 4, 7, 2}, 5))
                .isEqualTo(4);
        assertThat(longestSubarray.longestSubarray(new int[]{4, 2, 2, 2, 4, 4, 2, 2}, 2))
                .isEqualTo(3);
    }
}