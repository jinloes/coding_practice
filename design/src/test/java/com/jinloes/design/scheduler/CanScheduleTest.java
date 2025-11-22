package com.jinloes.design.scheduler;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CanScheduleTest {
    private CanSchedule canSchedule;


    @Test
    void canSchedule() {
        canSchedule = new CanSchedule(List.of(
                new CanSchedule.Movie("Lord Of The Rings", 120, 600),
                new CanSchedule.Movie("Back To The Future", 90, 500)
        ), List.of(
                new CanSchedule.Screening("Lord Of The Rings", 660),
                new CanSchedule.Screening("Lord Of The Rings", 840),
                new CanSchedule.Screening("Back To The Future", 1020),
                new CanSchedule.Screening("Lord Of The Rings", 1200)
        ));

        assertThat(canSchedule.canSchedule(new CanSchedule.Movie("Terminator", 100, 100)))
                .isFalse();
        assertThat(canSchedule.canSchedule(new CanSchedule.Movie("Terminator 2", 60, 100)))
                .isTrue();
        assertThat(canSchedule.canSchedule(new CanSchedule.Movie("Lord Of The Rings", 60, 600)))
                .isTrue();
    }

    @Test
    void canScheduleEmptySchedule() {
        canSchedule = new CanSchedule(List.of(), List.of());

        assertThat(canSchedule.canSchedule(new CanSchedule.Movie("Terminator", 13 * 60 + 1, 100)))
                .isFalse();
    }

    @Test
    void calculateRevenue() {
        canSchedule = new CanSchedule(List.of(
                new CanSchedule.Movie("Lord Of The Rings", 120, 600),
                new CanSchedule.Movie("Back To The Future", 90, 500)
        ), List.of(
                new CanSchedule.Screening("Lord Of The Rings", 660),
                new CanSchedule.Screening("Lord Of The Rings", 840),
                new CanSchedule.Screening("Back To The Future", 1020),
                new CanSchedule.Screening("Lord Of The Rings", 1200)
        ));

        assertThat(canSchedule.calculateRevenue()).isEqualTo(2300);
    }

    @Test
    void planScreening() {
        canSchedule = new CanSchedule(List.of(
                new CanSchedule.Movie("Lord Of The Rings", 120, 600),
                new CanSchedule.Movie("Back To The Future", 90, 500)
        ), List.of(
                new CanSchedule.Screening("Lord Of The Rings", 660),
                new CanSchedule.Screening("Lord Of The Rings", 840),
                new CanSchedule.Screening("Back To The Future", 1020),
                new CanSchedule.Screening("Lord Of The Rings", 1200)
        ));

        //assertThat(canSchedule.planScreening(new CanSchedule.Movie())).isEqualTo(2300);
    }
}