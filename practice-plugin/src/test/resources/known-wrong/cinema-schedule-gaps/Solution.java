package com.jinloes.practice;

import java.util.List;
import java.util.Map;

public class Solution {
    public record Movie(String title, int durationMinutes, int revenue) {
    }

    public record Screening(String title, int startMinute) {
    }

    private final Map<String, Movie> movies;
    private final List<Screening> screenings;

    public Solution(Map<String, Movie> movies, List<Screening> screenings) {
        this.movies = movies;
        this.screenings = screenings;
    }

    public boolean canSchedule(String title) {
        return movies.containsKey(title) && screenings.isEmpty();
    }

    public int calculateRevenue() {
        return 0;
    }
}
