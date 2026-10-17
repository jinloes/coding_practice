package com.jinloes.practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void sortsUnorderedScreeningsBeforeCheckingGaps() {
        Map<String, Solution.Movie> movies = movies(new Solution.Movie("A", 90, 10),
                new Solution.Movie("B", 100, 20), new Solution.Movie("Fit", 75, 30));
        Solution schedule = new Solution(movies, List.of(new Solution.Screening("B", time("14:00")),
                new Solution.Screening("A", time("10:30"))));
        assertThat(schedule.canSchedule("Fit")).isTrue();
        assertThat(schedule.calculateRevenue()).isEqualTo(30);
    }

    @Test
    void exactGapAtOpeningIsAllowed() {
        Map<String, Solution.Movie> movies = movies(new Solution.Movie("Later", 60, 10),
                new Solution.Movie("First", 120, 40));
        Solution schedule = new Solution(movies, List.of(new Solution.Screening("Later", time("12:00"))));
        assertThat(schedule.canSchedule("First")).isTrue();
    }

    @Test
    void unknownMovieCannotBeScheduled() {
        Solution schedule = new Solution(movies(new Solution.Movie("Known", 60, 15)), List.of());
        assertThat(schedule.canSchedule("Missing")).isFalse();
    }

    @Test
    void gapAfterLastScreeningCanBeUsed() {
        Map<String, Solution.Movie> movies = movies(new Solution.Movie("Matinee", 90, 50),
                new Solution.Movie("Late", 300, 80));
        Solution schedule = new Solution(movies, List.of(new Solution.Screening("Matinee", time("10:00"))));
        assertThat(schedule.canSchedule("Late")).isTrue();
    }

    @Test
    void fullDayBlocksLongCandidate() {
        Map<String, Solution.Movie> movies = movies(new Solution.Movie("Block", 390, 100),
                new Solution.Movie("Long", 391, 200));
        Solution schedule = new Solution(movies, List.of(new Solution.Screening("Block", time("10:00")),
                new Solution.Screening("Block", time("16:30"))));
        assertThat(schedule.canSchedule("Long")).isFalse();
    }

    private static Map<String, Solution.Movie> movies(Solution.Movie... movies) {
        Map<String, Solution.Movie> result = new HashMap<>();
        for (Solution.Movie movie : movies) {
            result.put(movie.title(), movie);
        }
        return result;
    }

    private static int time(String text) {
        String[] parts = text.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}
