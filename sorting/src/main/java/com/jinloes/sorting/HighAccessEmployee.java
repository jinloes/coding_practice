package com.jinloes.sorting;


import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class HighAccessEmployee {

    public List<String> findHighAccessEmployees(List<List<String>> access_times) {
        Map<String, List<LocalTime>> accessMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        for (List<String> access : access_times) {
            String user = access.get(0);
            LocalTime time = LocalTime.parse(access.get(1), formatter);

            List<LocalTime> userTimes = accessMap.getOrDefault(user, new ArrayList<>());
            userTimes.add(time);
            accessMap.put(user, userTimes);
        }

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, List<LocalTime>> entry : accessMap.entrySet()) {
            List<LocalTime> times = entry.getValue();

            if (times.size() < 3) {
                continue;
            }

            times.sort(Comparator.naturalOrder());

            for (int i = 0; i < times.size() - 2; i++) {
                if (Duration.between(times.get(i), times.get(i + 2)).compareTo(Duration.ofHours(1)) < 0) {
                    result.add(entry.getKey());
                    break;
                }
            }
        }

        return result;
    }
}