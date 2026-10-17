package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Workload {
    static final String UNIT = "screening";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {
    }

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int size = (Integer) state;
        Map<String, Solution.Movie> movies = new HashMap<>();
        List<Solution.Screening> screenings = new ArrayList<>();
        movies.put("Tiny", new Solution.Movie("Tiny", 1, 1));
        movies.put("Probe", new Solution.Movie("Probe", 2, 5));
        for (int i = 0; i < size; i++) {
            screenings.add(new Solution.Screening("Tiny", 600 + i * 2));
        }
        Solution schedule = new Solution(movies, screenings);
        schedule.canSchedule("Probe");
        if (schedule.calculateRevenue() != size) {
            throw new IllegalStateException("bad schedule");
        }
    }

    static int units(int size) {
        return size;
    }
}
