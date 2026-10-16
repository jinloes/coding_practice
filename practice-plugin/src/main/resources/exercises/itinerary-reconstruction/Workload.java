package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

/**
 * Scaling workload for Itinerary Reconstruction: {@code size} tickets forming size / 2 round trips
 * from one hub, listed in a scrambled order. Ticket construction happens outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int trips = size / 2;
        List<List<String>> tickets = new ArrayList<>(size);
        for (int i = 0; i < trips; i++) {
            String city = "C" + ((i * 7919L) % trips);
            tickets.add(List.of("HUB", city));
            tickets.add(List.of(city, "HUB"));
        }
        return tickets;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        List<List<String>> tickets = (List<List<String>>) state;
        List<String> route = Solution.buildItinerary(tickets, "HUB");
        if (route.size() != tickets.size() + 1 || !route.get(0).equals("HUB")) {
            throw new IllegalStateException("Itinerary does not use every ticket");
        }
    }

    static int units(int size) {
        return 1;
    }
}
