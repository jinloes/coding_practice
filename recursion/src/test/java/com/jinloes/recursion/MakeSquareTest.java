package com.jinloes.recursion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MakeSquareTest {
    private MakeSquare makeSquare;

    @BeforeEach
    void setUp() {
        makeSquare = new MakeSquare();
    }

    @Test
    void makesquare() {
        int[] matchsticks = new int[]{1, 3, 4, 2, 2, 4};
        assertThat(makeSquare.makesquare(matchsticks))
                .isTrue();

        matchsticks = new int[]{15, 13, 12, 9, 6, 3, 2};
        assertThat(makeSquare.makesquare(matchsticks))
                .isTrue();

        matchsticks = new int[]{15, 15, 13, 13, 13, 13, 12, 10, 9, 7, 4, 4, 4};
        assertThat(makeSquare.makesquare(matchsticks))
                .isFalse();

        matchsticks = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,102};
        assertThat(makeSquare.makesquare(matchsticks))
                .isFalse();
    }
}