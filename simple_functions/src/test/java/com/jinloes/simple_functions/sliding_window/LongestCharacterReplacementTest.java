package com.jinloes.simple_functions.sliding_window;

import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestCharacterReplacementTest {
    private LongestCharacterReplacement longestCharacterReplacement;

    @Before
    public void setUp() throws Exception {
        longestCharacterReplacement = new LongestCharacterReplacement();
    }

    @Test
    public void characterReplacement() {
        assertThat(longestCharacterReplacement.characterReplacement("AABABBA", 1))
                .isEqualTo(4);
    }


    @Test
    public void characterReplacement2() {
        assertThat(longestCharacterReplacement.characterReplacement("ABAB", 2))
                .isEqualTo(4);
    }

    @Test
    public void characterReplacement3() {
        assertThat(longestCharacterReplacement.characterReplacement("ABBB", 2))
                .isEqualTo(4);
    }

    @Test
    public void testDate() {

        OffsetDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime leapYear = OffsetDateTime.of(2020, 2, 29, 0, 0, 0, 0, ZoneOffset.UTC)
                .plusMonths(1);

        System.out.println(now.with(TemporalAdjusters.lastDayOfYear()).getDayOfYear());
        System.out.println(now.minusYears(1).with(TemporalAdjusters.lastDayOfYear()).getDayOfYear());

        System.out.println(leapYear.withDayOfYear(365));

        System.out.println(leapYear);

        int periodBoundaryDay = 365;

        OffsetDateTime periodStartDate = OffsetDateTime.of(2020, 12, 30, 0, 0, 0, 0, ZoneOffset.UTC);

        OffsetDateTime fullPeriodStartDate = periodStartDate;

        if (needsAdjustment(fullPeriodStartDate, periodBoundaryDay)) {
            fullPeriodStartDate = fullPeriodStartDate.minusYears(1);
        }

        fullPeriodStartDate = fullPeriodStartDate.with(temporal -> setDayOfYear(temporal, periodBoundaryDay));

        OffsetDateTime periodEndDate = fullPeriodStartDate.plusYears(1)
                .with(temporal -> setDayOfYear(temporal, periodBoundaryDay))
                .minusDays(1);

        System.out.println("Full period start date " + fullPeriodStartDate);
        System.out.println("Period start date " + periodStartDate);
        System.out.println("Period end date " + periodEndDate);
    }

    private boolean needsAdjustment(Temporal temporal, int periodBoundaryDay) {
        if (Year.isLeap(temporal.get(ChronoField.YEAR)) && periodBoundaryDay >= 59) {
            periodBoundaryDay++;
        }

        return temporal.get(ChronoField.DAY_OF_YEAR) < periodBoundaryDay;
    }

    private Temporal setDayOfYear(Temporal temporal, int dayOfYear) {
        // Adjust for lead years if the day is 59 or greater
        // In a non leap year day 59 is Feb 28 so we treat that as a month end boundary so
        // bump everything up in a leap to maintain consistency
        if (Year.isLeap(temporal.get(ChronoField.YEAR)) && dayOfYear >= 59) {
            dayOfYear++;
        }
        return temporal.with(ChronoField.DAY_OF_YEAR, dayOfYear);
    }
}