package com.jinloes.simple_functions;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxGuestTimeTest {
    private MaxGuestTime maxGuestTime;

    @Before
    public void setUp() throws Exception {
        maxGuestTime = new MaxGuestTime();
    }

    @Test
    public void maxGuests() {
        assertThat(maxGuestTime.maxGuests(new int[]{1, 2, 9, 5, 5}, new int[]{4, 5, 12, 9, 12}))
                .isEqualTo(5);
    }


    @Test
    public void maxGuests2() {
        int[] arrive = new int[]{13, 28, 29, 14, 40, 17, 3};
        int[] leave = new int[]{107, 95, 111, 105, 70, 127, 74};
        assertThat(maxGuestTime.maxGuests(arrive, leave))
                .isEqualTo(40);
    }

}