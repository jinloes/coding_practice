package com.jinloes.practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void featureFitsBetweenExistingShowingsAndClose() {
        Map<String, Solution.Movie> movies = movies(new Solution.Movie("Short", 60, 100),
                new Solution.Movie("Feature", 90, 250));
        Solution schedule = new Solution(movies, List.of(new Solution.Screening("Short", time("10:00")),
                new Solution.Screening("Short", time("12:00"))));
        assertThat(schedule.canSchedule("Feature")).isTrue();
        assertThat(schedule.calculateRevenue()).isEqualTo(200);
    }

    @Test
    void oversizedMovieDoesNotFit() {
        Map<String, Solution.Movie> movies = movies(new Solution.Movie("Short", 60, 100),
                new Solution.Movie("Epic", 700, 900));
        Solution schedule = new Solution(movies, List.of(new Solution.Screening("Short", time("12:00"))));
        assertThat(schedule.canSchedule("Epic")).isFalse();
        assertThat(schedule.calculateRevenue()).isEqualTo(100);
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
