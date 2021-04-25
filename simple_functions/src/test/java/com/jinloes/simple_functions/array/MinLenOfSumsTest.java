package com.jinloes.simple_functions.array;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MinLenOfSumsTest {
    private MinLenOfSums minLenOfSums;

    @Before
    public void setUp() throws Exception {
        minLenOfSums = new MinLenOfSums();
    }

    @Test
    public void minSumOfLengths() {
        assertThat(minLenOfSums.minSumOfLengths(new int[]{3, 2, 2, 4, 3}, 3)).isEqualTo(2);
        assertThat(minLenOfSums.minSumOfLengths(new int[]{7, 3, 4, 7}, 7)).isEqualTo(2);
        assertThat(minLenOfSums.minSumOfLengths(new int[]{4, 3, 2, 6, 2, 3, 4}, 6)).isEqualTo(-1);
        assertThat(minLenOfSums.minSumOfLengths(new int[]{5, 5, 4, 4, 5}, 3)).isEqualTo(-1);
        assertThat(minLenOfSums.minSumOfLengths(new int[]{3, 1, 1, 1, 5, 1, 2, 1}, 3)).isEqualTo(3);
    }
}