package com.jinloes.practice;

/**
 * Scaling workload for Cheapest Flights Within K Stops: {@code size} cities where each city has a
 * flight to the next (price 5) and the one after (price 7). The stop limit is fixed at 2, so the
 * work grows with the number of flights.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] flights = new int[2 * (size - 2)][];
        int next = 0;
        for (int city = 0; city < size - 2; city++) {
            flights[next++] = new int[]{city, city + 1, 5};
            flights[next++] = new int[]{city, city + 2, 7};
        }
        return flights;
    }

    static void run(Object state) {
        int[][] flights = (int[][]) state;
        int cities = flights.length / 2 + 2;
        if (Solution.cheapestPrice(cities, flights, 0, 3, 2) != 12) {
            throw new IllegalStateException("Unexpected cheapest price");
        }
    }

    static int units(int size) {
        return 1;
    }
}
