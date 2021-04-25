package com.jinloes.simple_functions.tree;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NumOfMinutesTest {
    private NumOfMinutes numOfMinutes;

    @Before
    public void setUp() throws Exception {
        numOfMinutes = new NumOfMinutes();
    }

    @Test
    public void numOfMinutesBfs() {
        assertThat(numOfMinutes.numOfMinutesBfs(11, 4, new int[]{5, 9, 6, 10, -1, 8, 9, 1, 9, 3, 4},
                new int[]{0, 213, 0, 253, 686, 170, 975, 0, 261, 309, 337}))
                .isEqualTo(2560);

    }

    @Test
    public void numOfMinutesDfs() {
        assertThat(numOfMinutes.numOfMinutesBfs(11, 4, new int[]{5, 9, 6, 10, -1, 8, 9, 1, 9, 3, 4},
                new int[]{0, 213, 0, 253, 686, 170, 975, 0, 261, 309, 337}))
                .isEqualTo(2560);
    }
}