package com.jinloes.design.most_popular;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MostPopularImplTest {
    private MostPopularImpl mostPopular;

    @BeforeEach
    void setUp() {
        mostPopular = new MostPopularImpl();
    }

    @Test
    void empty() {
        assertThat(mostPopular.mostPopular())
                .isEqualTo(-1);
    }

    @Test
    void testIncrease() {
        mostPopular.increasePopularity(7);

        assertThat(mostPopular.mostPopular())
                .isEqualTo(7);
    }

    @Test
    void testIncrease2() {
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(8);

        assertThat(mostPopular.mostPopular())
                .isEqualTo(7);
    }

    @Test
    void testIncrease3() {
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(8);
        mostPopular.increasePopularity(8);
        mostPopular.increasePopularity(8);

        assertThat(mostPopular.mostPopular())
                .isEqualTo(8);
    }

    @Test
    void decreaseEmpty() {
        mostPopular.increasePopularity(7);
        mostPopular.decreasePopularity(7);

        assertThat(mostPopular.mostPopular())
                .isEqualTo(-1);
    }

    @Test
    void tie() {
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(7);
        mostPopular.increasePopularity(8);
        mostPopular.increasePopularity(8);
        mostPopular.decreasePopularity(7);

        assertThat(mostPopular.mostPopular())
                .isIn(7, 8);
    }
}