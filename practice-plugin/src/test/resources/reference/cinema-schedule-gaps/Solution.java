package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Solution {
    private static final int OPEN = 10 * 60;
    private static final int CLOSE = 23 * 60;

    public record Movie(String title, int durationMinutes, int revenue) {
    }

    public record Screening(String title, int startMinute) {
    }

    private final Map<String, Movie> movies;
    private final List<Screening> screenings;
    private final int revenue;

    public Solution(Map<String, Movie> movies, List<Screening> screenings) {
        this.movies = Map.copyOf(movies);
        this.screenings = new ArrayList<>(screenings);
        this.screenings.sort(Comparator.comparingInt(Screening::startMinute));
        int total = 0;
        for (Screening screening : screenings) {
            Movie movie = movies.get(screening.title());
            if (movie != null) {
                total += movie.revenue();
            }
        }
        revenue = total;
    }

    public boolean canSchedule(String title) {
        Movie candidate = movies.get(title);
        if (candidate == null) {
            return false;
        }
        int previousEnd = OPEN;
        for (Screening screening : screenings) {
            Movie scheduled = movies.get(screening.title());
            if (scheduled == null) {
                continue;
            }
            if (screening.startMinute() - previousEnd >= candidate.durationMinutes()) {
                return true;
            }
            previousEnd = Math.max(previousEnd, screening.startMinute() + scheduled.durationMinutes());
        }
        return CLOSE - previousEnd >= candidate.durationMinutes();
    }

    public int calculateRevenue() {
        return revenue;
    }
}
