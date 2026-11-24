package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void picksEarliestOptimalClosingHourOnTie() {
        assertThat(Solution.bestClosingHour("YYNY"))
                .as("bestClosingHour(\"YYNY\") must return earliest minimum hour 2")
                .isEqualTo(2);
    }

    @Test
    void closesAtZeroWhenNoCustomersArrive() {
        assertThat(Solution.bestClosingHour("NNNN"))
                .as("bestClosingHour(\"NNNN\") must close immediately at hour 0")
                .isEqualTo(0);
    }

    @Test
    void staysOpenAllHoursWhenEveryHourHasCustomers() {
        assertThat(Solution.bestClosingHour("YYYY"))
                .as("bestClosingHour(\"YYYY\") must close at hour 4")
                .isEqualTo(4);
    }
}
