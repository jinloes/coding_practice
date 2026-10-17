package com.jinloes.practice;

import java.util.List;
import java.util.Map;

public class Solution {
    public record Movie(String title, int durationMinutes, int revenue) {
    }

    public record Screening(String title, int startMinute) {
    }

    public Solution(Map<String, Movie> movies, List<Screening> screenings) {
        throw new UnsupportedOperationException("Implement Solution");
    }

    public boolean canSchedule(String title) {
        throw new UnsupportedOperationException("Implement canSchedule");
    }

    public int calculateRevenue() {
        throw new UnsupportedOperationException("Implement calculateRevenue");
    }
}
