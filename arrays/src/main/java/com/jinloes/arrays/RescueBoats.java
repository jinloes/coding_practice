package com.jinloes.arrays;

import java.util.*;

class RescueBoats {
    public int numRescueBoats(int[] people, int limit) {
        int max = Arrays.stream(people)
                .max()
                .getAsInt();
        List<Integer> counts = new ArrayList<>();
        int boats = 0;

        for (int i = 0; i <= max; i++) {
            counts.add(0);
        }

        for (int weight : people) {
            int count = counts.size() < weight ? 0 : counts.get(weight);
            counts.set(weight, count + 1);
        }

        int start = 1;
        int end = max;

        while(counts.get(start) == 0) {
            start++;
        }

        while (start <= end) {
            int capacity = limit - end;
            boats++;

            int count = counts.get(end);
            counts.set(end, count - 1);

            while (start <= end && counts.get(end) == 0) {
                end--;
            }

            if (capacity >= start) {
                count = counts.get(start);
                counts.set(start, count - 1);
            }
            while (start <= end && counts.get(start) == 0) {
                start++;
            }
        }
        return boats;
    }
}
