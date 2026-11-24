package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};
    private static final int STOPS_PER_ROUTE = 8;

    private Workload() {
    }

    static Object prepare(int size) {
        int routeCount = Math.max(2, size / STOPS_PER_ROUTE);
        int[][] routes = new int[routeCount][STOPS_PER_ROUTE];
        int nextStop = 0;
        for (int r = 0; r < routeCount; r++) {
            routes[r][0] = r == 0 ? 0 : nextStop - 1;
            for (int k = 1; k < STOPS_PER_ROUTE; k++) {
                routes[r][k] = ++nextStop;
            }
        }
        return new Object[]{routes, nextStop};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        int[][] routes = (int[][]) data[0];
        int target = (Integer) data[1];
        int buses = new Solution().minBuses(routes, 0, target);
        if (buses != routes.length) {
            throw new IllegalStateException("Expected " + routes.length + " buses, got " + buses);
        }
    }

    static int units(int size) {
        return 1;
    }
}
