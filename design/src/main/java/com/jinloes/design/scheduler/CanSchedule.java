package com.jinloes.design.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanSchedule {
    public record Movie(String title, int duration, long revenue) {

    }

    public record Screening(String title, int startTime) {

    }

    public record MovieSchedule(String title, int startTime, int endTime) {

    }

    private final Map<String, Movie> movieMap;

    private final List<MovieSchedule> scheduleList;
    private long totalRevenue;

    public CanSchedule(List<Movie> movies, List<Screening> screenings) {
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.title, movie);
        }

        this.scheduleList = new ArrayList<>();
        scheduleList.add(new MovieSchedule("Cinema Open", 600, 600));

        for (Screening screening : screenings) {
            Movie movie = movieMap.get(screening.title);
            int duration = movie.duration;
            int startTime = screening.startTime;
            int endTime = startTime + duration;
            scheduleList.add(new MovieSchedule(screening.title, startTime, endTime));
            totalRevenue += movie.revenue;
        }

        int endTime = 23 * 60;
        scheduleList.add(new MovieSchedule("Cinema Close", endTime, endTime));

    }


    public boolean canSchedule(Movie movie) {
        for (int i = 0; i < scheduleList.size() - 1; i++) {
            MovieSchedule current = scheduleList.get(i);
            MovieSchedule next = scheduleList.get(i + 1);

            int durationBetween = next.startTime - current.endTime;
            if (durationBetween >= movie.duration()) {
                return true;
            }
        }

        return false;
    }


    public long calculateRevenue() {
        return totalRevenue;
    }
}
